package com.example.Card_Tracker.controller;

import com.example.Card_Tracker.Repository.PriceRepository;
import com.example.Card_Tracker.model.Price;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceRepository priceRepository;

    public PriceController(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @GetMapping("/{pokemonId}")
    public List<Price> getPricesByPokemonId(@PathVariable Long pokemonId) {
        return priceRepository.findByCardPokemonId(pokemonId);
    }
}