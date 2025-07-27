package com.example.Card_Tracker.controller;

import com.example.Card_Tracker.service.CardProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/cards")
public class CardProcessingController {

    private static final Logger logger = LoggerFactory.getLogger(CardProcessingController.class);

    @Autowired
    private CardProcessingService cardProcessingService;

    /**
     * Process cards from TCG service for a specific page range
     * GET /api/cards/process?startPage=1&endPage=5
     */
    @GetMapping("/process")
    public ResponseEntity<String> processCards(
            @RequestParam(defaultValue = "1") int startPage,
            @RequestParam(defaultValue = "5") int endPage) {

        try {
            logger.info("Processing cards from pages {} to {}", startPage, endPage);
            cardProcessingService.processCardsFromTCGService(startPage, endPage);
            cardProcessingService.logDatabaseStats();

            return ResponseEntity.ok(
                    String.format("Successfully processed cards from pages %d to %d", startPage, endPage));

        } catch (Exception e) {
            logger.error("Error processing cards", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing cards: " + e.getMessage());
        }
    }

    /**
     * Process all cards from TCG service (pages 1-200)
     * GET /api/cards/process-all
     */
    @GetMapping("/process-all")
    public ResponseEntity<String> processAllCards() {
        try {
            logger.info("Processing all cards from TCG service");
            cardProcessingService.processAllCardsFromTCGService();
            cardProcessingService.logDatabaseStats();

            return ResponseEntity.ok("Successfully processed all cards from TCG service");

        } catch (Exception e) {
            logger.error("Error processing all cards", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing all cards: " + e.getMessage());
        }
    }

    /**
     * Get database statistics
     * GET /api/cards/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<String> getDatabaseStats() {
        try {
            cardProcessingService.logDatabaseStats();
            return ResponseEntity.ok("Database statistics logged successfully");
        } catch (Exception e) {
            logger.error("Error getting database stats", e);
            return ResponseEntity.internalServerError()
                    .body("Error getting stats: " + e.getMessage());
        }
    }
}