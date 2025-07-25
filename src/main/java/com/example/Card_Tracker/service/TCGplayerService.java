package com.example.Card_Tracker.service;


import com.microsoft.playwright.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class TCGplayerService {

    @Value("${TCGBaseSearch}")
    private String tcgPlayerUrl;

    private Playwright playwright;
    private Browser browser;
    private Page page;

    public void initializeBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    public void navigateToTCGPlayer() {
        if (page == null) {
            initializeBrowser();
        }
        page.navigate(tcgPlayerUrl);
        page.waitForLoadState();


    }

    public void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    public List<>

}