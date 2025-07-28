package com.example.Card_Tracker.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Used for prices
public class Card {
    @Id
    private Long cardId;

    @ManyToOne
    @JoinColumn(name = "pokemon_id")
    private Pokemon pokemon;

    @ManyToOne
    @JoinColumn(name = "set_id")
    private Set cardSet; // renamed from 'set' to avoid confusion with Java's Set interface

    private String setNumber;
    private String priceURL;
    private String imageURL;

    // Default constructor
    public Card() {}

    // Public constructor
    public Card(Long cardId, String setNumber, Pokemon pokemon, Set cardSet, String imageURL) {
        this.cardId = cardId;
        this.setNumber = setNumber;
        this.pokemon = pokemon;
        this.cardSet = cardSet;
        this.imageURL = imageURL;
    }

    // Getters
    public Long getCardId() {
        return cardId;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public Set getCardSet() {
        return cardSet;
    }

    public String getSetNumber() {
        return setNumber;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getPriceURL() {
        return priceURL;
    }

    // Setters
    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public void setPokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public void setCardSet(Set cardSet) {
        this.cardSet = cardSet;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setPriceURL(String priceURL) {
        this.priceURL = priceURL;
    }

    // Convenience methods
    public String getSetName() {
        return cardSet != null ? cardSet.getName() : null;
    }

    public int getSetTotalCards() {
        return cardSet != null ? cardSet.getTotalCards() : 0;
    }
}