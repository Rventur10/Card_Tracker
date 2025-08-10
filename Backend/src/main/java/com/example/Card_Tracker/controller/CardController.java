package com.example.Card_Tracker.controller;

import com.example.Card_Tracker.Repository.CardRepository;
import com.example.Card_Tracker.dto.CardDataDTO;
import com.example.Card_Tracker.model.Card;
import com.example.Card_Tracker.integration.PokemonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/cards")
@CrossOrigin(origins = "http://localhost:4200") 
public class CardController {
    
    private final CardRepository cardRepository;
    private final PokemonClient pokemonClient; 
    
    public CardController(CardRepository cardRepository, PokemonClient pokemonClient) {
        this.cardRepository = cardRepository;
        this.pokemonClient = pokemonClient;
    }
    
    @GetMapping("/pokemon-name/{pokemonName}")
    public List<Card> getCardsByPokemonName(@PathVariable String pokemonName) {
        return cardRepository.findByPokemonName(pokemonName);
    }
    
    @GetMapping("/pokemon-name/{pokemonName}/set/{setNumber}")
    public ResponseEntity<Card> getSpecificCard(
            @PathVariable String pokemonName,
            @PathVariable String setNumber) {
        Optional<Card> card = cardRepository.findByPokemonNameAndSetNumber(pokemonName, setNumber);
        return card.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/set/{setId}")
    public ResponseEntity<List<CardDataDTO>> getCardsBySet(@PathVariable String setId) {
        try {
            List<CardDataDTO> cards = pokemonClient.getCardsBySet(setId);
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // NEW: Get all available sets from Pokemon TCG API (uses your Kotlin service)
    @GetMapping("/sets")
    public ResponseEntity<List<CardDataDTO.CardSetDTO>> getAllSets() {
        try {
            List<CardDataDTO.CardSetDTO> sets = pokemonClient.getAllSets();
            pokemonClient.cardConversion(sets);
            return ResponseEntity.ok(sets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }


    }
}