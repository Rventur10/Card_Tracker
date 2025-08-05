package com.example.Card_Tracker.Repository;

import com.example.Card_Tracker.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

 // Exact match: "Charizard 4/102" (matches full setNumber string)
 @Query("SELECT c FROM Card c WHERE c.pokemon.name = :pokemonName AND c.setNumber = :setNumber")
 Optional<Card> findByPokemonNameAndSetNumber(
         @Param("pokemonName") String pokemonName,
         @Param("setNumber") String setNumber
 );


 // All cards in a set (ordered by position)
 @Query("SELECT c FROM Card c WHERE c.cardSet.set_Id = :setId " +
         "ORDER BY CAST(SUBSTRING(c.setNumber, 1, LOCATE('/', c.setNumber) - 1) AS int)")
 List<Card> findBySetIdOrdered(@Param("setId") String setId);

 // All versions of a Pokemon (across all sets)
 @Query("SELECT c FROM Card c WHERE c.pokemon.name = :pokemonName")
 List<Card> findByPokemonName(@Param("pokemonName") String pokemonName);

 // Cards by Pokemon ID
 @Query("SELECT c FROM Card c WHERE c.pokemon.pokemon_Id = :pokemonId")
 List<Card> findByPokemonId(@Param("pokemonId") Long pokemonId);

 // Fuzzy name search
 @Query("SELECT c FROM Card c WHERE LOWER(c.pokemon.name) LIKE LOWER(CONCAT('%', :nameFragment, '%'))")
 List<Card> searchByNameContaining(@Param("nameFragment") String nameFragment);
}