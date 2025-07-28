package com.example.Card_Tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class CardDataDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("set")
    private CardSetDTO set;

    @JsonProperty("number")
    private String setNumber;

    @JsonProperty("id")
    private String pokemonTcgId;

    @JsonProperty("images")
    private CardImagesDTO images;

    // Default constructor
    public CardDataDTO() {}

    // Constructor for mapping
    public CardDataDTO(String name, CardSetDTO set, String setNumber, String pokemonTcgId, CardImagesDTO images) {
        this.name = name;
        this.set = set;
        this.setNumber = setNumber;
        this.pokemonTcgId = pokemonTcgId;
        this.images = images;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getSetName() {
        return set != null ? set.getName() : null;
    }

    public String getSetNumber() {
        return setNumber;
    }

    public String getPokemonTcgId() {
        return pokemonTcgId;
    }

    public String getImageURL() {
        return images != null ? images.getSmall() : null;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSet(CardSetDTO set) {
        this.set = set;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public void setPokemonTcgId(String pokemonTcgId) {
        this.pokemonTcgId = pokemonTcgId;
    }

    public void setImages(CardImagesDTO images) {
        this.images = images;
    }

    // Validation
    public boolean isValidCard() {
        return name != null && !name.trim().isEmpty() &&
                set != null && set.getName() != null && !set.getName().trim().isEmpty() &&
                setNumber != null && !setNumber.trim().isEmpty() &&
                pokemonTcgId != null && !pokemonTcgId.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("CardDataDTO{name='%s', setName='%s', setNumber='%s', pokemonTcgId='%s', imageURL='%s'}",
                name != null ? name : "null",
                getSetName() != null ? getSetName() : "null",
                setNumber != null ? setNumber : "null",
                pokemonTcgId != null ? pokemonTcgId : "null",
                getImageURL() != null ? getImageURL() : "null");
    }

    // Helper methods
    public String extractPokemonName() {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        String pokemonName = name.trim();
        String[] suffixes = {" V", " VMAX", " VSTAR", " ex", " GX", " Prime", " BREAK", " Tag Team"};

        for (String suffix : suffixes) {
            if (pokemonName.endsWith(suffix)) {
                return pokemonName.substring(0, pokemonName.length() - suffix.length()).trim();
            }
        }
        return pokemonName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardDataDTO that = (CardDataDTO) o;
        return Objects.equals(pokemonTcgId, that.pokemonTcgId) &&
                Objects.equals(setNumber, that.setNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pokemonTcgId, setNumber);
    }

    // Nested classes
    public static class CardSetDTO {
        @JsonProperty("name")
        private String name;

        @JsonProperty("id")
        private String setId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSetId() {
            return setId;
        }

        public void setSetId(String setId) {
            this.setId = setId;
        }
    }

    public static class CardImagesDTO {
        @JsonProperty("small")
        private String small;

        @JsonProperty("large")
        private String large;

        public String getSmall() {
            return small;
        }

        public void setSmall(String small) {
            this.small = small;
        }

        public String getLarge() {
            return large;
        }

        public void setLarge(String large) {
            this.large = large;
        }
    }
}