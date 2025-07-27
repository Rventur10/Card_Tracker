package com.example.Card_Tracker.controller;

import com.example.Card_Tracker.dto.CardDataDTO;
import com.example.Card_Tracker.service.TCGplayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class QuickTestController {

    @Autowired
    private TCGplayerService tcgPlayerService;

    @GetMapping("/scrape")
    public String testScraping() {
        try {
            List<CardDataDTO> cards = tcgPlayerService.processPage(1);

            StringBuilder result = new StringBuilder();
            result.append("Found ").append(cards.size()).append(" cards on page 1:\n\n");

            for (CardDataDTO card : cards) {
                result.append("Name: ").append(card.getName()).append("\n");
                result.append("Set: ").append(card.getSet()).append("\n");
                result.append("TCG ID: ").append(card.getTCGId()).append("\n");
                result.append("---\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        } finally {
            tcgPlayerService.closeBrowser();
        }
    }
}