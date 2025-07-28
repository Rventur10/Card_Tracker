package com.example.Card_Tracker.model;

import jakarta.persistence.*;

@Entity
public class Set {
    @Id
    private String set_Id; // same as pokemonTCG ID

    @Column(unique = true)
    private String name;

    private int totalCards;

    // Default constructor
    public Set() {}

    // Public constructors
    public Set(String set_Id, String name, int totalCards) {
        this.set_Id = set_Id;
        this.name = name;
        this.totalCards = totalCards;
    }

    // Public setters
    public void setSet_Id(String set_Id) {
        this.set_Id = set_Id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }

    // Public getters
    public String getSet_Id() {
        return set_Id;
    }

    public String getName() {
        return name;
    }

    public int getTotalCards() {
        return totalCards;
    }
}