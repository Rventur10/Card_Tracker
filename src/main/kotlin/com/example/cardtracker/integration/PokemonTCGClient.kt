package com.example.cardtracker.integration

import com.example.Card_Tracker.dto.CardDataDTO
import com.example.Card_Tracker.dto.CardDataDTO.CardSetDTO
import com.example.cardtracker.service.PokemonTcgService

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.retry.annotation.Backoff

@Component
class PokemonClient(
    private val pokemonTcgService: PokemonTcgService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Gets all cards from a specific set with automatic pagination
     * @param setId The Pokémon TCG set ID (e.g. "swsh4")
     * @return List of all cards in the set
     */
    @Retryable(maxAttempts = 3, backoff = Backoff(delay = 1000))
    fun getCardsBySet(setId: String): List<CardDataDTO> {
        logger.info("Fetching all cards for set: $setId")
        return pokemonTcgService.getCardsFromSet(setId).also {
            logger.debug("Retrieved ${it.size} cards for set $setId")
        }
    }

    /**
     * Gets a specific card by its set number
     * @param setId The set ID
     * @param number The card number (e.g. "12/102")
     * @return The matching card or null if not found
     */
    fun getCardBySetNumber(setId: String, number: String): CardDataDTO? {
        logger.debug("Looking for card $number in set $setId")
        return pokemonTcgService.getCardsFromSet(setId)
            .firstOrNull { it.setNumber == number }
    }

    /**
     * Gets all available sets (cached)
     * @return List of all Pokémon TCG sets
     */
    @Cacheable("pokemonSets")
    fun getAllSets(): List<CardSetDTO> {
        logger.info("Fetching all Pokémon TCG sets")
        return pokemonTcgService.getSets().also {
            logger.debug("Retrieved ${it.size} sets")
        }
    }

    /**
     * Gets a specific set by ID
     * @param setId The set ID (e.g. "swsh12")
     * @return The matching set or null if not found
     */
    fun getSetById(setId: String): CardSetDTO? {
        logger.debug("Looking for set with ID: $setId")
        return getAllSets().firstOrNull { it.setId == setId }
    }

    /**
     * Gets all cards for a specific Pokémon
     * @param pokemonName The Pokémon name (e.g. "Charizard")
     * @return List of all cards featuring this Pokémon
     */
    fun getCardsByPokemon(pokemonName: String): List<CardDataDTO> {
        logger.info("Fetching all cards for Pokémon: $pokemonName")
        return getAllSets().flatMap { set ->
            pokemonTcgService.getCardsFromSet(set.setId)
                .filter { it.extractPokemonName()?.equals(pokemonName, ignoreCase = true) ?: false }
        }.also {
            logger.debug("Found ${it.size} cards for $pokemonName")
        }
    }
}