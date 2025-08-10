package com.example.Card_Tracker.integration;

import com.example.Card_Tracker.dto.CardDataDTO;
import com.example.Card_Tracker.dto.CardDataDTO.CardSetDTO;
import com.example.Card_Tracker.Repository.SetRepository;
import com.example.Card_Tracker.model.Card_Set;
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

    public PokemonClient(PokemonTcgService pokemonTcgService, SetRepository setRepository) {
        this.pokemonTcgService = pokemonTcgService;
        this.setRepository = setRepository;
    }
    
    /**
     * Gets all cards from a specific set with automatic pagination
     * @param setId The Pokémon TCG set ID (e.g. "swsh4")
     * @return List of all cards in the set
     */
    public List<CardDataDTO> getCardsBySet(String setId) {
        logger.info("Fetching all cards for set: {}", setId);
        List<CardDataDTO> cards = pokemonTcgService.getCardsFromSet(setId);
        logger.debug("Retrieved {} cards for set {}", cards.size(), setId);
        return cards;
    }
    
    /**
     * Gets all available sets (cached)
     * @return List of all Pokémon TCG sets
     */
    @Cacheable("pokemonSets")
    public List<CardSetDTO> getAllSets() {
        logger.info("Fetching all Pokémon TCG sets");
        List<CardSetDTO> sets = pokemonTcgService.getSets();
        logger.debug("Retrieved {} sets", sets.size());
        return sets;
    }

    public void cardConversion(List<CardSetDTO> cardSets){

        for (CardDataDTO.CardSetDTO cardSet : cardSets){
            Card_Set entity = new Card_Set(
                    cardSet.getSetId(),
                    cardSet.getName(),
                    cardSet.getTotalCards()

            );
            setRepository.save(entity);

        }
    }
}