package com.example.Card_Tracker.controller;

import com.example.Card_Tracker.service.EbayWebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Controller
public class Webhook {

    private static final Logger logger = LoggerFactory.getLogger(Webhook.class);

    private final EbayWebhookService ebayWebhookService;

    // Constructor injection
    public Webhook(EbayWebhookService ebayWebhookService) {
        this.ebayWebhookService = ebayWebhookService;
    }

    /**
     * Handles eBay's initial challenge code verification
     * This is called when you first set up the webhook with eBay
     */
    @GetMapping("/webhooks/ebay-account-deletion/")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleEbayChallengeCode(
            @RequestParam("challenge_code") String challengeCode) {

        logger.info("Received eBay challenge code request");

        try {
            Map<String, String> response = ebayWebhookService.handleChallengeCode(challengeCode);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid challenge request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Missing required parameters"));
        } catch (Exception e) {
            logger.error("Error processing challenge code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Server error"));
        }
    }

    /**
     * Handles actual account deletion notifications from eBay
     * This is called when a user deletes their eBay account
     */
    @PostMapping("/webhooks/ebay-account-deletion/")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleEbayAccountDeletion(
            @RequestBody String payload,
            @RequestHeader Map<String, String> headers) {

        logger.info("Received eBay account deletion webhook");
        logger.info("Webhook headers: {}", headers);
        logger.info("Webhook payload: {}", payload);

        // Get the signature header (case insensitive)
        String xEbaySignature = headers.get("x-ebay-signature");
        if (xEbaySignature == null) {
            xEbaySignature = headers.get("X-Ebay-Signature");
        }

        if (xEbaySignature == null) {
            logger.warn("Missing X-Ebay-Signature header in webhook request");

            return ResponseEntity.ok(new HashMap<>());
        }

        boolean success = ebayWebhookService.handleAccountDeletion(xEbaySignature, payload);

        if (success) {
            logger.info("Successfully processed eBay account deletion webhook");
            return ResponseEntity.ok(new HashMap<>());
        } else {
            // IMPORTANT: Still return 200 OK to acknowledge receipt

            return ResponseEntity.ok(new HashMap<>());
        }
    }

    /**
     * Helper method to create error responses
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}