package com.example.Card_Tracker.Repository;


import com.example.Card_Tracker.model.Price;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long>{



    @Query("SELECT p FROM Price p WHERE p.card.cardId = :cardId " +
            "AND (:psa10 IS NULL OR p.psa10 IS NOT NULL) " +
            "AND (:psa9 IS NULL OR p.psa9 IS NOT NULL) " +
            "AND (:nm IS NULL OR p.nm IS NOT NULL) " +
            "AND (:lp IS NULL OR p.lp IS NOT NULL) " +
            "AND (:mp IS NULL OR p.mp IS NOT NULL) " +
            "AND (:dmg IS NULL OR p.dmg IS NOT NULL)")

    List<Price> findPricesByCardIdAndConditions(
            @Param("cardId") Long cardId,
            @Param("psa10") Boolean psa10,
            @Param("psa9") Boolean psa9,
            @Param("nm") Boolean nm,
            @Param("lp") Boolean lp,
            @Param("mp") Boolean mp,
            @Param("dmg") Boolean dmg



    );

}