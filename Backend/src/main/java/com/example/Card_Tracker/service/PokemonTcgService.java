package com.example.Card_Tracker.service;

import com.example.Card_Tracker.dto.CardDataDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



@Service
public class PokemonTcgService {
    
    private static final int MAX_PAGE_SIZE = 250;
    private static final Logger logger = LoggerFactory.getLogger(PokemonTcgService.class);
    
    private final RestTemplate restTemplate;
    private final String cardBaseUrl = "https://api.pokemontcg.io/v2/cards";
    private final String cardSetUrl = "https://api.pokemontcg.io/v2/cards?q=set.id:";
    private final String setBaseUrl = "https://api.pokemontcg.io/v2/sets";
    
    @Value("${POKEMON_TCG_API_KEY}")
    private String apiKey;
    
    public PokemonTcgService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public List<CardDataDTO> getCardsFromSet(String setId) {
        List<CardDataDTO> allCards = new ArrayList<>();
        int currentPage = 1;
        boolean hasMorePages = true;
        
        while (hasMorePages) {
            String url = String.format("%s?q=set.id:%s&page=%d&pageSize=%d", 
                cardBaseUrl, setId, currentPage, MAX_PAGE_SIZE);
            
            try {
                ResponseEntity<PokemonApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createRequestEntity(),
                    PokemonApiResponse.class
                );
                
                PokemonApiResponse body = response.getBody();
                if (body == null) {
                    break;
                }
                
                allCards.addAll(body.getData());
                
                // Check if we need to fetch more pages
                hasMorePages = body.getData().size() == MAX_PAGE_SIZE;
                currentPage++;
                
                logger.debug("Fetched page {} for set {}, total cards: {}", 
                    currentPage, setId, allCards.size());
                
            } catch (HttpClientErrorException e) {
                logger.error("Client error fetching cards for set {} (page {}): {} - {}", 
                    setId, currentPage, e.getStatusCode(), e.getResponseBodyAsString());
                break;
            } catch (HttpServerErrorException e) {
                logger.error("Server error fetching cards for set {} (page {}): {} - {}", 
                    setId, currentPage, e.getStatusCode(), e.getResponseBodyAsString());
                break;
            } catch (Exception e) {
                logger.error("Unexpected error fetching cards for set {} (page {})", setId, currentPage, e);
                break;
            }
        }
        
        return allCards;
    }
    
    public List<CardDataDTO.CardSetDTO> getSets() {
        try {
            String url = String.format("%s?pageSize=%d", setBaseUrl, MAX_PAGE_SIZE);
            
            ResponseEntity<PokemonSetApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                createRequestEntity(),
                PokemonSetApiResponse.class
            );
            
            PokemonSetApiResponse body = response.getBody();
            return body != null ? body.getData() : Collections.emptyList();
            
        } catch (HttpClientErrorException e) {
            logger.error("Client error fetching sets: {} - {}", 
                e.getStatusCode(), e.getResponseBodyAsString());
            return Collections.emptyList();
        } catch (HttpServerErrorException e) {
            logger.error("Server error fetching sets: {} - {}", 
                e.getStatusCode(), e.getResponseBodyAsString());
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Unexpected error fetching sets", e);
            return Collections.emptyList();
        }
    }
    
    private HttpEntity<String> createRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiKey);
        headers.set("Accept", "application/json");
        return new HttpEntity<>(headers);
    }
    
    // Inner classes for API responses
    public static class PokemonApiResponse {
        @JsonProperty("data")
        private List<CardDataDTO> data = new ArrayList<>();
        
        @JsonProperty("count")
        private int count = 0;
        
        @JsonProperty("totalCount")
        private int totalCount = 0;
        
        @JsonProperty("page")
        private int page = 1;
        
        @JsonProperty("pageSize")
        private int pageSize = MAX_PAGE_SIZE;
        
        // Default constructor
        public PokemonApiResponse() {}
        
        // Getters and setters
        public List<CardDataDTO> getData() { return data; }
        public void setData(List<CardDataDTO> data) { this.data = data; }
        
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    }
    
    public static class PokemonSetApiResponse {
        @JsonProperty("data")
        private List<CardDataDTO.CardSetDTO> data = new ArrayList<>();
        
        // Default constructor
        public PokemonSetApiResponse() {}
        
        // Getters and setters
        public List<CardDataDTO.CardSetDTO> getData() { return data; }
        public void setData(List<CardDataDTO.CardSetDTO> data) { this.data = data; }
    }
}