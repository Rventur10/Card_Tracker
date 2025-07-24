package com.example.Card_Tracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Used for prices
public class Card {
    @Id

    private Long cardId;

    @ManyToOne
    @JoinColumn(name = "pokemon_id")
    private Pokemon pokemon;

    private String setNumber; //The way to identify all cards of the same pokemon. Used in Ebay searches
    private String imageURL;

    // Default constructor
    public Card() {}

    // Public constructor
    public Card(String setNumber, Pokemon pokemon, String imageURL) {
        this.setNumber = setNumber;
        this.imageURL = imageURL;
        this.pokemon = pokemon;
    }

    // Getters
    public Long getCardId() {
        return cardId;
    }

    public Pokemon getPokemon() {
        return pokemon; // Returns the full Pokemon object
    }

    public Long getPokemonId() {
        return pokemon.getPokemon_Id(); // Returns just the Pokemon ID for sorting/mapping
    }

    public String getPokemonName() {
        return pokemon.getName(); // Returns just the Pokemon name for eBay searches
    }

    public String getSetNumber() {
        return setNumber;
    }

    public String getImageURL() {
        return imageURL;
    }

    // Setters
    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public void setPokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}