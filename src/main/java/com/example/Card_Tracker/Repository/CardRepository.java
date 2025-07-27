package com.example.Card_Tracker.Repository;

import com.example.Card_Tracker.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>{



   //search by name and set i.e. Charizard 4/102
    @Query("SELECT c FROM Card c WHERE c.pokemon.name = :pokemonName AND c.setNumber = :setNumber")
    Optional<Card> findByPokemonNameAndSetNumber(@Param("pokemonName") String pokemonName, @Param("setNumber") String setNumber);

    // Name only
    @Query("SELECT c FROM Card c WHERE c.pokemon.name = :pokemonName")
    List<Card> findByPokemonName(@Param("pokemonName") String pokemonName);


    // I case I need to search by ID
    @Query("SELECT c FROM Card c WHERE c.pokemon.pokemon_Id = :pokemonId")
    List<Card> findByPokemonId(@Param("pokemonId") Long pokemonId);
}