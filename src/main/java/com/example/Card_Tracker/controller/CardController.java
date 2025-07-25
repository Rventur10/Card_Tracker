package com.example.Card_Tracker.controller;

import com.example.Card_Tracker.Repository.CardRepository;
import com.example.Card_Tracker.model.Card;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardRepository cardRepository;

    public CardController(CardRepository cardRepository) {
        this.cardRepository = cardRepository;

    }

    // Method 2: Get cards by Pokemon name (uses existing method)
    @GetMapping("/pokemon-name/{pokemonName}")
    public List<Card> getCardsByPokemonName(@PathVariable String pokemonName) {
        return cardRepository.findByPokemonName(pokemonName);
    }

    // Method 3: Get specific card by Pokemon name and set number
    @GetMapping("/pokemon-name/{pokemonName}/set/{setNumber}")
    public ResponseEntity<Card> getSpecificCard(
            @PathVariable String pokemonName,
            @PathVariable String setNumber) {
        Optional<Card> card = cardRepository.findByPokemonNameAndSetNumber(pokemonName, setNumber);
        return card.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}