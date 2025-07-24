package com.example.Card_Tracker.Repository;


import com.example.Card_Tracker.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long>{

    //Find by name
    Optional<Pokemon> findByName(String name);


    //check if already exists
    boolean existsByName(String name);

    //Find names that contain a string
    List<Pokemon> findByNameContainingIgnoreeCase(String nameFragment);

    //Find all sorted asc
    List<Pokemon> findAllByOrderedByNameAsc();

    //Find all sorted dec
    List<Pokemon> findAllByOrderedByNameDesc();

}
