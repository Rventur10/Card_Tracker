package com.example.Card_Tracker.controller;

import com.example.Card_Tracker.Repository.CardRepository;
import com.example.Card_Tracker.model.Card;
import com.example.Card_Tracker.model.Pokemon;
import com.example.Card_Tracker.model.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardRepository cardRepository;


    private Card testCard;
    private Pokemon testPokemon;
    private Set testSet;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public CardRepository cardRepository() {
            return Mockito.mock(CardRepository.class);
        }
    }

    @BeforeEach
    void setUp() {
        testPokemon = new Pokemon("Charizard");
        testPokemon.setPokemon_Id(1L);

        testSet = new Set("base1", "Base Set", 102);

        testCard = new Card(1L, "4/102", testPokemon, testSet, "http://example.com/image.jpg");

        // Reset the mock before each test to ensure clean state
        Mockito.reset(cardRepository);
    }

    @Test
    void getCardsByPokemonName_ReturnsCards() throws Exception {
        when(cardRepository.findByPokemonName("Charizard"))
                .thenReturn(Arrays.asList(testCard));

        mockMvc.perform(get("/cards/pokemon-name/Charizard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].pokemon.name").value("Charizard"))
                .andExpect(jsonPath("$[0].setNumber").value("4/102"));
    }

    @Test
    void checkExistingCard() throws Exception {
        when(cardRepository.findByPokemonNameAndSetNumber("Charizard", "4/102"))
                .thenReturn(Optional.of(testCard));

        mockMvc.perform(get("/cards/pokemon-name/Charizard/set/4/102"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pokemon.name").value("Charizard"))
                .andExpect(jsonPath("$.setNumber").value("4/102"));
    }

    @Test
    void checkExistingCardCaseSensitive() throws Exception {
        when(cardRepository.findByPokemonNameAndSetNumber("charizard", "4/102"))
                .thenReturn(Optional.of(testCard));

        mockMvc.perform(get("/cards/pokemon-name/charizard/set/4/102"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pokemon.name").value("Charizard"))
                .andExpect(jsonPath("$.setNumber").value("4/102"));
    }

    @Test
    void checkExistingCardNotFullSet() throws Exception {
        when(cardRepository.findByPokemonNameAndSetNumber("Charizard", "4"))
                .thenReturn(Optional.of(testCard));

        mockMvc.perform(get("/cards/pokemon-name/Charizard/set/4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pokemon.name").value("Charizard"))
                .andExpect(jsonPath("$.setNumber").value("4/102"));
    }

    @Test
    void checkNonExistingCard_ReturnsNotFound() throws Exception {
        // Mock repository to return empty Optional for non-existing card
        when(cardRepository.findByPokemonNameAndSetNumber("Caesar", "114/102"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/cards/pokemon-name/Caesar/set/114/102"))
                .andExpect(status().isNotFound());
    }
}