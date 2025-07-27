package com.example.Card_Tracker;

import com.example.Card_Tracker.dto.CardDataDTO;
import com.example.Card_Tracker.service.TCGplayerService;

import java.util.List;

public class QuickTest {
    public static void main(String[] args) {
        TCGplayerService service = new TCGplayerService();

        try {
            System.out.println("Testing page 1...");
            List<CardDataDTO> cards = service.processPage(10);

            System.out.println("Found " + cards.size() + " cards:");
            for (CardDataDTO card : cards) {
                System.out.println("- " + card.getName() + " | Set: " + card.getSet() + " | TCG ID: " + card.getTCGId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            service.closeBrowser();
        }
    }
}