package com.example.Card_Tracker.controller;

import com.example.Card_Tracker.Repository.CardRepository;
import com.example.Card_Tracker.model.Card;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardRepository cardRepository;

    public CardController(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @GetMapping("/{pokemonId}")
    public List<Card> getCardsByPokemonId(@PathVariable Long pokemonId) {
        return cardRepository.findByPokemonId(pokemonId);
    }
}