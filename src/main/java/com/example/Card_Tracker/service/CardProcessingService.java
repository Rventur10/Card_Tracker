package com.example.Card_Tracker.service;

import com.example.Card_Tracker.dto.CardDataDTO;
import com.example.Card_Tracker.model.Card;
import com.example.Card_Tracker.model.Pokemon;
import com.example.Card_Tracker.Repository.CardRepository;
import com.example.Card_Tracker.Repository.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CardProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(CardProcessingService.class);

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private TCGplayerService tcgplayerService;

    /**
     * Process and save a single CardDataDTO to the database
     */
    public Card processAndSaveCard(CardDataDTO cardData) {
        if (cardData == null || !cardData.isValidCard()) {
            logger.warn("Invalid card data provided: {}", cardData);
            return null;
        }

        try {
            // Check if card already exists by TCG ID
            if (cardData.getTCGId() != null && !cardData.getTCGId().trim().isEmpty()) {
                try {
                    Long tcgId = Long.parseLong(cardData.getTCGId());
                    Optional<Card> existingCard = cardRepository.findById(tcgId);

                    if (existingCard.isPresent()) {
                        logger.info("Card already exists with TCG ID: {}", tcgId);
                        return existingCard.get();
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Invalid TCG ID format for card {}: {}", cardData.getName(), cardData.getTCGId());
                    return null;
                }
            } else {
                logger.warn("No TCG ID provided for card: {}", cardData.getName());
                return null;
            }

            // Get or create Pokemon
            Pokemon pokemon = getOrCreatePokemon(cardData.getName());

            // Create new Card
            Card newCard = new Card(cardData.getSet(), pokemon, cardData.getImageURL());

            // Set the TCG ID as the card ID
            if (cardData.getTCGId() != null && !cardData.getTCGId().trim().isEmpty()) {
                try {
                    Long tcgId = Long.parseLong(cardData.getTCGId());
                    newCard.setCardId(tcgId);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid TCG ID format for card {}: {}", cardData.getName(), cardData.getTCGId());
                    return null;
                }
            } else {
                logger.warn("No TCG ID provided for card: {}", cardData.getName());
                return null;
            }

            // Save the card
            Card savedCard = cardRepository.save(newCard);
            logger.info("Successfully saved card: {} {} with ID: {}",
                    savedCard.getPokemonName(), savedCard.getSetNumber(), savedCard.getCardId());

            return savedCard;

        } catch (Exception e) {
            logger.error("Error processing card data: {} - {}", cardData, e.getMessage());
            throw new RuntimeException("Failed to process card: " + cardData.getName(), e);
        }
    }

    /**
     * Process and save multiple CardDataDTO objects
     */
    public void processAndSaveCards(List<CardDataDTO> cardDataList) {
        if (cardDataList == null || cardDataList.isEmpty()) {
            logger.warn("No card data provided to process");
            return;
        }

        logger.info("Processing {} cards", cardDataList.size());

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (CardDataDTO cardData : cardDataList) {
            try {
                Card result = processAndSaveCard(cardData);
                if (result != null) {
                    successCount++;
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                logger.error("Failed to process card: {}", cardData, e);
                errorCount++;
            }
        }

        logger.info("Card processing complete. Success: {}, Skipped: {}, Errors: {}",
                successCount, skipCount, errorCount);
    }

    /**
     * Get existing Pokemon or create a new one
     */
    private Pokemon getOrCreatePokemon(String pokemonName) {
        // Check if Pokemon already exists
        Optional<Pokemon> existingPokemon = pokemonRepository.findByName(pokemonName);

        if (existingPokemon.isPresent()) {
            return existingPokemon.get();
        }

        // Create new Pokemon
        Pokemon newPokemon = new Pokemon(pokemonName);
        Pokemon savedPokemon = pokemonRepository.save(newPokemon);
        logger.info("Created new Pokemon: {} with ID: {}",
                savedPokemon.getName(), savedPokemon.getPokemon_Id());

        return savedPokemon;
    }

    /**
     * Process cards from TCG service for a specific page range
     */
    public void processCardsFromTCGService(int startPage, int endPage) {
        try {
            logger.info("Starting TCG card processing for pages {}-{}", startPage, endPage);

            tcgplayerService.initializeBrowser();
            List<CardDataDTO> cardDataList = tcgplayerService.processPages(startPage, endPage);

            if (!cardDataList.isEmpty()) {
                processAndSaveCards(cardDataList);
            } else {
                logger.warn("No cards retrieved from TCG service");
            }

        } catch (Exception e) {
            logger.error("Error during TCG card processing", e);
            throw new RuntimeException("TCG card processing failed", e);
        } finally {
            tcgplayerService.closeBrowser();
        }
    }

    /**
     * Process all cards from TCG service
     */
    public void processAllCardsFromTCGService() {
        try {
            logger.info("Starting complete TCG card processing");

            tcgplayerService.initializeBrowser();
            List<CardDataDTO> cardDataList = tcgplayerService.processAllPages();

            if (!cardDataList.isEmpty()) {
                processAndSaveCards(cardDataList);
            } else {
                logger.warn("No cards retrieved from TCG service");
            }

        } catch (Exception e) {
            logger.error("Error during complete TCG card processing", e);
            throw new RuntimeException("Complete TCG card processing failed", e);
        } finally {
            tcgplayerService.closeBrowser();
        }
    }

    /**
     * Get statistics about processed cards
     */
    public void logDatabaseStats() {
        long totalCards = cardRepository.count();
        long totalPokemon = pokemonRepository.count();

        logger.info("Database Statistics - Total Cards: {}, Total Pokemon: {}",
                totalCards, totalPokemon);
    }
}