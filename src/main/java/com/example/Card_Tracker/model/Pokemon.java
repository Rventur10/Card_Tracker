package com.example.Card_Tracker.model;

import jakarta.persistence.*;


@Entity
public class Pokemon{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pokemon_Id;

    @Column(unique = true)
    private String name;


    // Default constructor
    public Pokemon(){}


    // Public constructors
    public Pokemon(String name){
        this.name = name;
    }


    // Public setters
    public void setPokemon_Id(Long pokemon_Id){
        this.pokemon_Id = pokemon_Id;
    }
    public void setName(String name){
        this.name = name;
    }


    // Public getters
    public Long getPokemon_Id(){
        return pokemon_Id;
    }
    public String getName(){
        return name;
    }


}




