package com.example.Card_Tracker.controller;

import com.example.Card_Tracker.Repository.PriceRepository;
import com.example.Card_Tracker.model.Price;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceRepository priceRepository;

    public PriceController(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;

    }

    @GetMapping("/prices/{cardId}")
    public List<Price> findPricesByCardIdAndConditions(@PathVariable Long cardId) {
        return priceRepository.findPricesByCardIdAndConditions(cardId, false, false, true, false, false, false);

    }


    @GetMapping("/prices/{cardId}/condition")
    public List<Price> findPricesByCardIdAndConditions(
            @PathVariable Long cardId,
            @RequestParam(required = false) Boolean psa10,
            @RequestParam(required = false) Boolean psa9,
            @RequestParam(required = false) Boolean nm,
            @RequestParam(required = false) Boolean lp,
            @RequestParam(required = false) Boolean mp,
            @RequestParam(required = false) Boolean dmg
            ) {

        return priceRepository.findPricesByCardIdAndConditions(cardId, psa10,psa9, nm, lp, mp, dmg);



    }
}