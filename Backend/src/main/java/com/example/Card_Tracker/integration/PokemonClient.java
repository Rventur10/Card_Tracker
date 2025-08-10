package com.example.Card_Tracker.integration;

import com.example.Card_Tracker.dto.CardDataDTO;
import com.example.Card_Tracker.dto.CardDataDTO.CardSetDTO;
import com.example.Card_Tracker.Repository.SetRepository;
import com.example.Card_Tracker.Repository.CardRepository;
import com.example.Card_Tracker.Repository.PokemonRepository;
import com.example.Card_Tracker.model.Card_Set;
import com.example.Card_Tracker.model.Card;
import com.example.Card_Tracker.model.Pokemon;
import com.example.Card_Tracker.service.PokemonTcgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PokemonClient {

    private static final Logger logger = LoggerFactory.getLogger(PokemonClient.class);
    private final PokemonTcgService pokemonTcgService;
    private final SetRepository setRepository;
    private final CardRepository cardRepository;
    private final PokemonRepository pokemonRepository;

    public PokemonClient(PokemonTcgService pokemonTcgService,
                         SetRepository setRepository,
                         CardRepository cardRepository,
                         PokemonRepository pokemonRepository) {
        this.pokemonTcgService = pokemonTcgService;
        this.setRepository = setRepository;
        this.cardRepository = cardRepository;
        this.pokemonRepository = pokemonRepository;
    }


    public List<CardDataDTO> getCardsBySet(String setId) {
        logger.info("Fetching all cards for set: {}", setId);
        List<CardDataDTO> cards = pokemonTcgService.getCardsFromSet(setId);
        logger.debug("Retrieved {} cards for set {}", cards.size(), setId);
        return cards;
    }


    public void cardProcess(List<CardDataDTO> cards) {
        for (CardDataDTO cardDto : cards) {
            try {
                // Find or create the Pokemon entity
                Pokemon pokemon = findOrCreatePokemon(cardDto.getName());

                // Find the existing Card_Set by ID
                Card_Set cardSet = setRepository.findBySetId(cardDto.getSet().getSetId())
                        .orElseThrow(() -> new RuntimeException("Card set not found: " + cardDto.getSet().getSetId()));

                // Create the Card entity
                Card cardEntity = new Card(
                        extractCardIdNumber(cardDto.getPokemonTcgId()),
                        cardDto.getSetNumber(),
                        pokemon,
                        cardSet,
                        cardDto.getImageURL()
                );

                cardRepository.save(cardEntity);
                logger.debug("Saved card: {} from set: {}", cardDto.getName(), cardDto.getSetName());

            } catch (Exception e) {
                logger.error("Error processing card: {} - {}", cardDto.getName(), e.getMessage());
            }
        }
    }


    private Pokemon findOrCreatePokemon(String pokemonName) {
        return pokemonRepository.findByName(pokemonName)
                .orElseGet(() -> {
                    Pokemon newPokemon = new Pokemon();
                    newPokemon.setName(pokemonName);
                    Pokemon savedPokemon = pokemonRepository.save(newPokemon);
                    logger.debug("Created new Pokemon: {}", pokemonName);
                    return savedPokemon;
                });
    }


    private Long extractCardIdNumber(String pokemonTcgId) {
        if (pokemonTcgId == null || pokemonTcgId.isEmpty()) {
            return null;
        }

        try {
            // Split by hyphen and take the last part
            String[] parts = pokemonTcgId.split("-");
            if (parts.length >= 2) {
                return Long.parseLong(parts[parts.length - 1]);
            }
            // If no hyphen, try to parse the whole string
            return Long.parseLong(pokemonTcgId);
        } catch (NumberFormatException e) {
            logger.warn("Could not extract numeric ID from: {}", pokemonTcgId);
            return null;
        }
    }


    @Cacheable("pokemonSets")
    public List<CardSetDTO> getAllSets() {
        logger.info("Fetching all Pokémon TCG sets");
        List<CardSetDTO> sets = pokemonTcgService.getSets();
        logger.debug("Retrieved {} sets", sets.size());
        return sets;
    }


    public void cardConversion(List<CardSetDTO> cardSets) {
        for (CardDataDTO.CardSetDTO cardSet : cardSets) {
            try {
                // Check if the set already exists to avoid duplicates
                if (!setRepository.findBySetId(cardSet.getSetId()).isPresent()) {
                    Card_Set entity = new Card_Set(
                            cardSet.getSetId(),
                            cardSet.getName(),
                            cardSet.getTotalCards()
                    );
                    setRepository.save(entity);
                    logger.debug("Saved new card set: {} ({})", cardSet.getName(), cardSet.getSetId());
                } else {
                    logger.debug("Card set already exists: {} ({})", cardSet.getName(), cardSet.getSetId());
                }
            } catch (Exception e) {
                logger.error("Error processing card set: {} - {}", cardSet.getName(), e.getMessage());
            }
        }
    }
}