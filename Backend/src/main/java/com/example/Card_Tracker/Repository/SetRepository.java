package com.example.Card_Tracker.Repository;

import com.example.Card_Tracker.model.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SetRepository extends JpaRepository<Set, String> {

    // Exact set name match
    Optional<Set> findByName(String name);

    // Fuzzy set name search
    @Query("SELECT s FROM Set s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :nameFragment, '%'))")
    List<Set> findByNameContainingIgnoreCase(@Param("nameFragment") String nameFragment);


    // All sets ordered A-Z
    @Query("SELECT s FROM Set s ORDER BY s.name ASC")
    List<Set> findAllOrderedByName();

    // Sets containing a specific card (by Pokemon name)
    @Query("SELECT DISTINCT c.cardSet FROM Card c WHERE c.pokemon.name = :pokemonName")
    List<Set> findSetsContainingPokemon(@Param("pokemonName") String pokemonName);
}