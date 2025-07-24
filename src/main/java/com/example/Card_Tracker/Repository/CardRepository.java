package com.example.Card_Tracker.Repository;


import com.example.Card_Tracker.model.Card;
import com.example.Card_Tracker.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>{

    Optional<Card> findByPokemonNameAndSetNumber(String setNumber, Pokemon pokemon);

    @Query("SELECT c FROM Card c WHERE c.pokemon.name = :pokemonName AND c.setNumber = :setNumber")
    Optional<Card> findByPokemonNameAndSetNumber(@Param("pokemonName") String pokemonName, @Param("setNumber") String setNumber);

    //Get all cards by name
    List<Card> findByPokemon(Pokemon pokemon);
    List<Card> findByPokemonName(String pokemonName);



}