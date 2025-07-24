package com.example.Card_Tracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    private BigDecimal psa10;
    private BigDecimal psa9;
    private BigDecimal nm;
    private BigDecimal lp;
    private BigDecimal mp;
    private BigDecimal dmg;


    private LocalDate pullDate;


    // Default constructor
    public Price(){}

    public Price(BigDecimal psa10, BigDecimal psa9, BigDecimal dmg, BigDecimal nm,
                 BigDecimal lp, BigDecimal mp, Card card, LocalDate pullDate) {
        this.psa10 = psa10;
        this.psa9 = psa9;
        this.nm = nm;
        this.lp = lp;
        this.mp = mp;
        this.dmg = dmg;
        this.card = card;
        this.pullDate = pullDate;
    }

    //getters
    public Card getCard(){
        return card;
    }

    public BigDecimal getDmg() {
        return dmg;
    }
    public BigDecimal getLp() {
        return lp;
    }
    public BigDecimal getMp() {
        return mp;
    }

    public BigDecimal getNm() {
        return nm;
    }

    public BigDecimal getPsa9() {
        return psa9;
    }

    public BigDecimal getPsa10() {
        return psa10;
    }

    public LocalDate getPullDate() {
        return pullDate;
    }

    public Long getPriceId() {
        return priceId;
    }
    //setters

    public void setCard(Card card) {
        this.card = card;
    }

    public void setDmg(BigDecimal dmg) {
        this.dmg = dmg;
    }

    public void setLp(BigDecimal lp) {
        this.lp = lp;
    }

    public void setMp(BigDecimal mp) {
        this.mp = mp;
    }

    public void setNm(BigDecimal nm) {
        this.nm = nm;
    }

    public void setPsa9(BigDecimal psa9) {
        this.psa9 = psa9;
    }

    public void setPsa10(BigDecimal psa10) {
        this.psa10 = psa10;
    }

    public void setPullDate(LocalDate pullDate) {
        this.pullDate = pullDate;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }
}