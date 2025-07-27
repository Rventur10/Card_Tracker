package com.example.Card_Tracker.dto;

public class CardDataDTO {

    public String name;
    public String set;
    public String TCGId;
    public String imageURL;

    // Default constructor
    public CardDataDTO() {}

    // Constructor with main fields
    public CardDataDTO(String name, String set) {
        this.name = name;
        this.set = set;
    }

    // Full constructor
    public CardDataDTO(String name, String set, String TCGId, String imageURL) {
        this.name = name;
        this.set = set;
        this.TCGId = TCGId;
        this.imageURL = imageURL;
    }

    // Public Getters
    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public String getSet() {
        return set;
    }

    public String getTCGId() {
        return TCGId;
    }

    // Public Setters
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public void setTCGId(String TCGId) {
        this.TCGId = TCGId;
    }

    // Validation method
    public boolean isValidCard() {
        return name != null && !name.trim().isEmpty() &&
                set != null && !set.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("CardDataDTO{name='%s', set='%s', TCGId='%s', imageURL='%s'}",
                name, set, TCGId, imageURL);
    }
}
