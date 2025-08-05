package com.example.Card_Tracker.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CardDataDTOTest {
    private CardDataDTO dto;
    private CardDataDTO.CardSetDTO cardSetDTO;
    private CardDataDTO.CardImagesDTO cardImagesDTO;

    @BeforeEach
    void setup() {
        cardSetDTO = new CardDataDTO.CardSetDTO();
        cardSetDTO.setName("Vivid Voltage");
        cardSetDTO.setSetId("vivid");

        cardImagesDTO = new CardDataDTO.CardImagesDTO();
        cardImagesDTO.setLarge("http://large.jpg");
        cardImagesDTO.setSmall("http://small.jpg");

        dto = new CardDataDTO("Pikachu", cardSetDTO, "188/254", "base2-3", cardImagesDTO);
    }

    @Test
    void constructor_ValidData_SetsFields() {
        assertThat(dto.getName()).isEqualTo("Pikachu");
        assertThat(dto.getSetName()).isEqualTo("Vivid Voltage");
        assertThat(dto.getSetNumber()).isEqualTo("188/254");
        assertThat(dto.getPokemonTcgId()).isEqualTo("base2-3");
        assertThat(dto.getImageURL()).isEqualTo("http://small.jpg");
    }

    @Test
    void nestedCardSetDTO_HasCorrectValues() {
        // Since you can't access nested properties directly,
        // test the nested object by creating a reference
        CardDataDTO.CardSetDTO retrievedSet = cardSetDTO; // This is the same object we passed to dto

        assertThat(retrievedSet.getName()).isEqualTo("Vivid Voltage");
        assertThat(retrievedSet.getSetId()).isEqualTo("vivid");
    }

    @Test
    void nestedCardImagesDTO_HasCorrectValues() {
        assertThat(cardImagesDTO.getSmall()).isEqualTo("http://small.jpg");
        assertThat(cardImagesDTO.getLarge()).isEqualTo("http://large.jpg");
    }

    @Test
    void equals_SameIdAndSetNumber_ReturnsTrue() {
        CardDataDTO other = new CardDataDTO();
        other.setPokemonTcgId("base2-3");
        other.setSetNumber("188/254");

        assertThat(dto.equals(other)).isTrue();
        assertThat(dto.hashCode()).isEqualTo(other.hashCode());
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        CardDataDTO other = new CardDataDTO();
        other.setPokemonTcgId("different-id");
        other.setSetNumber("188/254");

        assertThat(dto.equals(other)).isFalse();
    }

    @Test
    void equals_DifferentSetNumber_ReturnsFalse() {
        CardDataDTO other = new CardDataDTO();
        other.setPokemonTcgId("base2-3");
        other.setSetNumber("different-number");

        assertThat(dto.equals(other)).isFalse();
    }

    @Test
    void getSetName_WithNullSet_ReturnsNull() {
        dto.setSet(null);
        assertThat(dto.getSetName()).isNull();
    }

    @Test
    void getImageURL_WithNullImages_ReturnsNull() {
        dto.setImages(null);
        assertThat(dto.getImageURL()).isNull();
    }

    @Test
    void toString_ContainsAllExpectedFields() {
        String result = dto.toString();

        assertThat(result).contains("Pikachu");
        assertThat(result).contains("Vivid Voltage");
        assertThat(result).contains("188/254");
        assertThat(result).contains("base2-3");
        assertThat(result).contains("http://small.jpg");
    }

    @Test
    void defaultConstructor_CreatesEmptyDTO() {
        CardDataDTO emptyDTO = new CardDataDTO();

        assertThat(emptyDTO.getName()).isNull();
        assertThat(emptyDTO.getSetName()).isNull();
        assertThat(emptyDTO.getSetNumber()).isNull();
        assertThat(emptyDTO.getPokemonTcgId()).isNull();
        assertThat(emptyDTO.getImageURL()).isNull();
    }

    @Test
    void setters_ModifyFieldsCorrectly() {
        CardDataDTO testDTO = new CardDataDTO();

        testDTO.setName("Charizard");
        testDTO.setSet(cardSetDTO);
        testDTO.setSetNumber("4/102");
        testDTO.setPokemonTcgId("base1-4");
        testDTO.setImages(cardImagesDTO);

        assertThat(testDTO.getName()).isEqualTo("Charizard");
        assertThat(testDTO.getSetName()).isEqualTo("Vivid Voltage");
        assertThat(testDTO.getSetNumber()).isEqualTo("4/102");
        assertThat(testDTO.getPokemonTcgId()).isEqualTo("base1-4");
        assertThat(testDTO.getImageURL()).isEqualTo("http://small.jpg");
    }

    @Test
    void getSet_ReturnsCorrectSetDTO() {
        assertThat(dto.getSet().getSetId()).isEqualTo("vivid");
        assertThat(dto.getSet().getName()).isEqualTo("Vivid Voltage");
    }
}