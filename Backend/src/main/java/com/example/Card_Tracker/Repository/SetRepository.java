package com.example.Card_Tracker.Repository;

import com.example.Card_Tracker.model.Card_Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SetRepository extends JpaRepository<Card_Set, String> {

    // ADD THIS METHOD - Find by set ID
    Optional<Card_Set> findBySetId(String setId);

    // Exact set name match
    Optional<Card_Set> findByName(String name);

    // Fuzzy set name search
    @Query("SELECT s FROM Card_Set s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :nameFragment, '%'))")
    List<Card_Set> findByNameContainingIgnoreCase(@Param("nameFragment") String nameFragment);

    // All sets ordered A-Z
    List<Card_Set> findAllByOrderByNameAsc();

    // Sets containing a specific card (by Pokemon name)
    @Query("SELECT DISTINCT c.cardSet FROM Card c WHERE c.pokemon.name = :pokemonName")
    List<Card_Set> findSetsContainingPokemon(@Param("pokemonName") String pokemonName);
}