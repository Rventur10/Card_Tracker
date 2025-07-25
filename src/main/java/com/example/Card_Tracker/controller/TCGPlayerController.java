package com.example.Card_Tracker.controller;

import com.example.Card_Tracker.service.TCGplayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/navigate")
public class TCGPlayerController {

    private final TCGplayerService tcgPlayerService;

    public TCGPlayerController(TCGplayerService tcgPlayerService) {
        this.tcgPlayerService = tcgPlayerService;
    }

    @GetMapping("/TCGPlayer")
    public void navigateToTCGPlayer() {
        tcgPlayerService.navigateToTCGPlayer();
    }

    @GetMapping("/closeBrowser")
    public void closeBrowser() {
        tcgPlayerService.closeBrowser();
    }
}