package com.example.Card_Tracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class EbayWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(EbayWebhookService.class);

    @Value("${ebay.verification.token:}")
    private String verificationToken;

    @Value("${ebay.endpoint.url:}")
    private String endpointUrl;

    @Value("${ebay.base64.authorization.token:}")
    private String ebayBase64AuthorizationToken;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Handle eBay challenge code verification
     */
    public Map<String, String> handleChallengeCode(String challengeCode) {
        if (challengeCode == null || challengeCode.isEmpty() ||
                verificationToken == null || verificationToken.isEmpty() ||
                endpointUrl == null || endpointUrl.isEmpty()) {
            throw new IllegalArgumentException("Missing required parameters");
        }

        try {
            String challengeResponse = generateChallengeResponse(challengeCode);
            Map<String, String> response = new HashMap<>();
            response.put("challengeResponse", challengeResponse);
            return response;
        } catch (Exception e) {
            logger.error("Error generating challenge response", e);
            throw new RuntimeException("Error generating challenge response", e);
        }
    }

    /**
     * Handle eBay account deletion notification
     */
    public boolean handleAccountDeletion(String xEbaySignature, String payload) {
        if (xEbaySignature == null) {
            logger.warn("Missing X-Ebay-Signature header");
            return false;
        }

        try {
            // Decode the X-Ebay-Signature header
            String decodedSignature = new String(Base64.getDecoder().decode(xEbaySignature));
            JsonNode signatureJson = objectMapper.readTree(decodedSignature);
            String kid = signatureJson.get("kid").asText();
            String signature = signatureJson.get("signature").asText();
            String algorithm = signatureJson.has("alg") ? signatureJson.get("alg").asText() : "rsa";
            String digest = signatureJson.has("digest") ? signatureJson.get("digest").asText() : "SHA1";

            logger.info("Signature algorithm: {}, digest: {}", algorithm, digest);

            // Get the public key
            String publicKeyString = getPublicKey(kid);
            if (publicKeyString == null) {
                logger.warn("Failed to retrieve public key for kid: {}", kid);
                return false;
            }

            // Verify the signature
            if (!verifySignature(publicKeyString, signature, payload, algorithm, digest)) {
                logger.warn("Signature verification failed");
                return false;
            }

            // Process the account deletion
            processAccountDeletion(payload);
            return true;

        } catch (Exception e) {
            logger.error("Error processing account deletion webhook", e);
            return false;
        }
    }

    private String generateChallengeResponse(String challengeCode) throws Exception {
        String input = challengeCode + verificationToken + endpointUrl;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes("UTF-8"));

        // Convert to hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String getPublicKey(String kid) {
        try {
            String url = "https://api.ebay.com/commerce/notification/v1/public_key/" + kid;
            String oauthToken = getOauthToken();

            if (oauthToken == null) {
                logger.warn("Failed to get OAuth token");
                return null;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + oauthToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(url,
                    org.springframework.http.HttpMethod.GET, entity, JsonNode.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().get("key").asText();
            } else {
                logger.warn("Failed to get public key. Status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Failed to fetch public key", e);
        }
        return null;
    }

    private String getOauthToken() {
        if (ebayBase64AuthorizationToken == null || ebayBase64AuthorizationToken.isEmpty()) {
            logger.warn("eBay authorization token not configured");
            return null;
        }

        try {
            String url = "https://api.ebay.com/identity/v1/oauth2/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + ebayBase64AuthorizationToken);

            String payload = "grant_type=client_credentials&scope=https%3A%2F%2Fapi.ebay.com%2Foauth%2Fapi_scope";
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, entity, JsonNode.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().get("access_token").asText();
            } else {
                logger.warn("Failed to get OAuth token. Status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve OAuth token", e);
        }
        return null;
    }

    private boolean verifySignature(String publicKeyString, String signature, String payload,
                                    String algorithm, String digest) {
        try {
            // Format the public key
            String formattedKey = formatPublicKey(publicKeyString);

            // Remove PEM headers and decode base64
            String keyContent = formattedKey
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(keyContent);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);

            // Determine key factory and signature algorithm based on the algorithm type
            KeyFactory keyFactory;
            String signatureAlgorithm;

            if ("ecdsa".equalsIgnoreCase(algorithm)) {
                keyFactory = KeyFactory.getInstance("EC");
                // For ECDSA, use SHA1withECDSA or SHA256withECDSA based on digest
                if ("SHA256".equalsIgnoreCase(digest)) {
                    signatureAlgorithm = "SHA256withECDSA";
                } else {
                    signatureAlgorithm = "SHA1withECDSA";
                }
            } else {
                // Default to RSA
                keyFactory = KeyFactory.getInstance("RSA");
                if ("SHA256".equalsIgnoreCase(digest)) {
                    signatureAlgorithm = "SHA256withRSA";
                } else {
                    signatureAlgorithm = "SHA1withRSA";
                }
            }

            PublicKey publicKey = keyFactory.generatePublic(spec);

            // Verify signature
            Signature sig = Signature.getInstance(signatureAlgorithm);
            sig.initVerify(publicKey);
            sig.update(payload.getBytes());

            byte[] signatureBytes = Base64.getDecoder().decode(signature);
            boolean verified = sig.verify(signatureBytes);

            logger.info("Signature verification result: {} using algorithm: {}", verified, signatureAlgorithm);
            return verified;

        } catch (Exception e) {
            logger.error("Error verifying signature with algorithm: {}, digest: {}", algorithm, digest, e);
            return false;
        }
    }

    private String formatPublicKey(String publicKey) {
        if (publicKey.length() < 50) {
            return publicKey;
        }

        StringBuilder formatted = new StringBuilder();
        formatted.append("-----BEGIN PUBLIC KEY-----\n");

        int index = 0;
        while (index < publicKey.length()) {
            int endIndex = Math.min(index + 64, publicKey.length());
            formatted.append(publicKey.substring(index, endIndex)).append("\n");
            index = endIndex;
        }

        formatted.append("-----END PUBLIC KEY-----");
        return formatted.toString();
    }

    private void processAccountDeletion(String payload) {
        logger.info("Received eBay account deletion notification: {}", payload);

        try {
            JsonNode payloadJson = objectMapper.readTree(payload);

            // This satisfies eBay's compliance requirements
            if (payloadJson.has("notification") &&
                    payloadJson.get("notification").has("data") &&
                    payloadJson.get("notification").get("data").has("userId")) {
                String userId = payloadJson.get("notification").get("data").get("userId").asText();
                logger.info("Acknowledged account deletion for eBay user: {}", userId);
            }

            // No actual data deletion needed since we only store card data,
            // not eBay user account information
            logger.info("Account deletion processing complete - no user data stored");

        } catch (Exception e) {
            logger.error("Error parsing account deletion payload", e);
        }
    }
}