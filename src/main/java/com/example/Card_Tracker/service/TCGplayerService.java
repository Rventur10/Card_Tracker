package com.example.Card_Tracker.service;

import com.example.Card_Tracker.dto.CardDataDTO;
import com.microsoft.playwright.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class TCGplayerService {

    private static final Logger logger = LoggerFactory.getLogger(TCGplayerService.class);

    @Value("${tcgplayer.base.url:https://www.tcgplayer.com/search/pokemon/product?productLineName=pokemon&view=grid&RarityName=Rare+BREAK|Shiny+Rare|Classic+Collection|Special+Illustration+Rare|Hyper+Rare|Shiny+Holo+Rare|Illustration+Rare|Secret+Rare|Black+White+Rare|Shiny+Ultra+Rare|Ultra+Rare|Promo}")
    private String tcgPlayerBaseUrl;

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    public void initializeBrowser() {
        try {
            playwright = Playwright.create();
            browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            context = browser.newContext();
            page = context.newPage();
            logger.info("Browser initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize browser", e);
            throw new RuntimeException("Browser initialization failed", e);
        }
    }

    public void closeBrowser() {
        try {
            if (context != null) {
                context.close();
            }
            if (browser != null) {
                browser.close();
            }
            if (playwright != null) {
                playwright.close();
            }
            logger.info("Browser closed successfully");
        } catch (Exception e) {
            logger.error("Error closing browser", e);
        }
    }

    private String buildUrlWithPage(int pageNumber) {
        return tcgPlayerBaseUrl + "&page=" + pageNumber;
    }

    public List<CardDataDTO> processPage(int pageNumber) {
        if (page == null) {
            initializeBrowser();
        }

        try {
            String pageUrl = buildUrlWithPage(pageNumber);
            logger.info("Navigating to: {}", pageUrl);

            page.navigate(pageUrl);
            page.waitForLoadState();

            // Wait for the search results to load
            page.waitForSelector("div.search-result", new Page.WaitForSelectorOptions().setTimeout(10000));

            logger.info("Processing page {}", pageNumber);

            List<CardDataDTO> validCards = new ArrayList<>();

            // Process cards on current page (0-23 indices)
            for (int i = 0; i < 24; i++) {
                CardDataDTO card = fetchCardData(i);
                if (card != null && card.isValidCard()) {
                    validCards.add(card);
                }
            }

            logger.info("Found {} valid cards on page {}", validCards.size(), pageNumber);
            return validCards;

        } catch (Exception e) {
            logger.error("Error processing page {}: {}", pageNumber, e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<CardDataDTO> processPages(int startPage, int endPage) {
        List<CardDataDTO> allCards = new ArrayList<>();

        for (int pageNum = startPage; pageNum <= endPage; pageNum++) {
            List<CardDataDTO> pageCards = processPage(pageNum);
            allCards.addAll(pageCards);

            // Add delay between pages to be respectful to the server
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("Completed processing pages {}-{}. Found {} total cards",
                startPage, endPage, allCards.size());
        return allCards;
    }

    public List<CardDataDTO> processAllPages() {
        return processPages(1, 200);
    }

    // Fixed card data fetching based on the actual HTML structure
    private CardDataDTO fetchCardData(int index) {
        try {
            String parentTestId = String.format("product-card__image--%d", index);
            Locator parentElement = page.locator(String.format("a[data-testid=\"%s\"]", parentTestId));

            if (!parentElement.isVisible()) {
                logger.debug("Parent element not visible for index {}", index);
                return null;
            }

            // Extract name from span.product-card__title.truncate
            Locator nameElement = parentElement.locator("span.product-card__title.truncate");
            if (!nameElement.isVisible()) {
                logger.debug("Name element not visible for index {}", index);
                return null;
            }
            String fullName = nameElement.innerText().trim();

            // Use the improved card name extraction method
            String name = extractCardName(fullName);

            // Extract set number from the rarity section
            // Based on your HTML: section.product-card__rarity.bottom-margin div.product-card__rarity__variant span:nth-child(2)
            Locator raritySection = parentElement.locator("section.product-card__rarity.bottom-margin");
            String setNumber = null;

            if (raritySection.isVisible()) {
                // Look for the second span which contains the set number (#77/73)
                Locator setNumberSpan = raritySection.locator("div.product-card__rarity__variant span:nth-child(2)");
                if (setNumberSpan.isVisible()) {
                    String setNumberText = setNumberSpan.innerText().trim();
                    // Remove the # symbol if present
                    if (setNumberText.startsWith("#")) {
                        setNumber = setNumberText.substring(1);
                    } else {
                        setNumber = setNumberText;
                    }
                }
            }

            // Extract set name from h4.product-card__set-name
            String setName = null;
            Locator setNameElement = parentElement.locator("h4.product-card__set-name div.product-card__set-name__variant");
            if (setNameElement.isVisible()) {
                setName = setNameElement.innerText().trim();
            }

            // Extract TCG ID from href attribute
            String tcgId = extractTcgId(parentElement);

            // Create and return CardDataDTO
            CardDataDTO cardData = new CardDataDTO();
            cardData.setName(name);
            cardData.setSet(setNumber);
            cardData.setTCGId(tcgId);

            // If you have a setName field in your DTO, uncomment this:
            // cardData.setSetName(setName);

            logger.debug("Extracted card: name='{}', set='{}', setName='{}', tcgId='{}'",
                    name, setNumber, setName, tcgId);

            return cardData;

        } catch (Exception e) {
            logger.debug("Could not process card at index {}: {}", index, e.getMessage());
            return null;
        }
    }

    // Improved card name extraction method
    private String extractCardName(String fullName) {
        // Step 1: Remove everything after " - " (card numbers)
        String name = fullName;
        if (fullName.contains(" - ")) {
            name = fullName.substring(0, fullName.indexOf(" - "));
        }

        // Step 2: Remove card type suffixes
        String[] suffixes = {" VSTAR", " VMAX", " ex", " gx", " v", " Star"};
        for (String suffix : suffixes) {
            if (name.toLowerCase().endsWith(suffix.toLowerCase())) {
                name = name.substring(0, name.length() - suffix.length());
                break;
            }
        }

        // Step 3: Handle trainer cards - remove trainer names (anything ending with 's + space)
        name = name.replaceAll("^.*'s\\s+", "");

        // Step 4: Handle other prefixes
        name = name.replaceAll("^(Basic\\s+|Team\\s+Rocket's\\s+|Dark\\s+|Galarian\\s+|Hisuian\\s+|Paldean\\s+|Teal\\s+Mask\\s+|Flying\\s+|Surfing\\s+)", "");

        return name.trim();
    }

    // Extract TCG ID from href attribute
    private String extractTcgId(Locator parentElement) {
        try {
            String href = parentElement.getAttribute("href");
            if (href != null && !href.isEmpty()) {
                logger.debug("Extracting TCG ID from href: {}", href);

                // For URLs like "/product/223076/pokemon-champions-path-kabu-secret?page=1"
                if (href.contains("/product/")) {
                    String[] parts = href.split("/product/");
                    if (parts.length > 1) {
                        String productPart = parts[1];
                        // Get the number part before the next slash
                        String[] productParts = productPart.split("/");
                        String tcgId = productParts[0];

                        // Remove any query parameters
                        if (tcgId.contains("?")) {
                            tcgId = tcgId.split("\\?")[0];
                        }

                        logger.debug("Extracted TCG ID: {}", tcgId);
                        return tcgId;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Could not extract TCG ID from href: {}", e.getMessage());
        }
        return null;
    }

    // Alternative method with more robust element finding
    private CardDataDTO fetchCardDataRobust(int index) {
        try {
            String parentTestId = String.format("product-card__image--%d", index);
            Locator parentElement = page.locator(String.format("a[data-testid=\"%s\"]", parentTestId));

            // Wait for the element to be visible
            if (!parentElement.isVisible()) {
                return null;
            }

            CardDataDTO cardData = new CardDataDTO();

            // Try multiple approaches to get the name
            String name = null;

            // Method 1: Direct selector
            Locator nameElement = parentElement.locator("span.product-card__title.truncate");
            if (nameElement.isVisible()) {
                name = nameElement.innerText().trim();
            }

            // Method 2: If first method fails, try finding by text content
            if (name == null || name.isEmpty()) {
                Locator allSpans = parentElement.locator("span");
                for (int i = 0; i < allSpans.count(); i++) {
                    Locator span = allSpans.nth(i);
                    String className = span.getAttribute("class");
                    if (className != null && className.contains("product-card__title")) {
                        name = span.innerText().trim();
                        break;
                    }
                }
            }

            if (name == null || name.isEmpty()) {
                return null;
            }

            // Use the improved card name extraction method
            cardData.setName(extractCardName(name));

            // Extract set number
            String setNumber = null;
            Locator rarityElements = parentElement.locator("section.product-card__rarity span");
            for (int i = 0; i < rarityElements.count(); i++) {
                String text = rarityElements.nth(i).innerText().trim();
                if (text.contains("#")) {
                    setNumber = text.replace("#", "").trim();
                    break;
                }
            }
            cardData.setSet(setNumber);

            // Extract TCG ID
            String tcgId = extractTcgId(parentElement);
            cardData.setTCGId(tcgId);

            logger.debug("Robust extraction - name='{}', set='{}', tcgId='{}'",
                    cardData.getName(), setNumber, tcgId);

            return cardData;

        } catch (Exception e) {
            logger.debug("Robust method failed for index {}: {}", index, e.getMessage());
            return null;
        }
    }
}