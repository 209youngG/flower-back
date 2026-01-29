package com.flower.curation.service;

import com.flower.curation.dto.FlowerLanguageDto;
import com.flower.curation.enums.Occasion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FlowerLanguageServiceTest {

    @Autowired
    private FlowerLanguageService flowerLanguageService;

    @Test
    @DisplayName("고백 상황에는 빨간 장미, 빨간 튤립이 추천되어야 한다")
    void should_recommendRoses_when_occasionIsConfession() {
        // given
        Occasion occasion = Occasion.CONFESSION;

        // when
        List<FlowerLanguageDto> flowers = flowerLanguageService.findByOccasion(occasion);

        // then
        assertThat(flowers).isNotEmpty();
        assertThat(flowers).extracting("flowerName")
                .contains("빨간 장미", "빨간 튤립");
    }

    @Test
    @DisplayName("위로 상황에는 흰 국화, 백합이 추천되어야 한다")
    void should_recommendComfortFlowers_when_occasionIsComfort() {
        // given
        Occasion occasion = Occasion.COMFORT;

        // when
        List<FlowerLanguageDto> flowers = flowerLanguageService.findByOccasion(occasion);

        // then
        assertThat(flowers).extracting("flowerName")
                .contains("흰 국화", "백합");
    }

    @Test
    @DisplayName("감사 상황에는 노란 장미, 카네이션이 추천되어야 한다")
    void should_recommendGratitudeFlowers_when_occasionIsGratitude() {
        // given
        Occasion occasion = Occasion.GRATITUDE;

        // when
        List<FlowerLanguageDto> flowers = flowerLanguageService.findByOccasion(occasion);

        // then
        assertThat(flowers).extracting("flowerName")
                .contains("노란 장미", "카네이션");
    }

    @Test
    @DisplayName("특정 꽃의 모든 꽃말을 조회할 수 있다")
    void should_returnAllMeanings_when_searchByFlowerName() {
        // given
        String flowerName = "빨간 장미";

        // when
        List<FlowerLanguageDto> meanings = flowerLanguageService.findByFlowerName(flowerName);

        // then
        assertThat(meanings).isNotEmpty();
        assertThat(meanings).extracting("meaning")
                .contains("당신을 사랑합니다");
    }

    @Test
    @DisplayName("상황과 감정을 함께 필터링할 수 있다")
    void should_filterByOccasionAndEmotion() {
        // given
        Occasion occasion = Occasion.CONFESSION;
        String emotion = "LOVE";

        // when
        List<FlowerLanguageDto> flowers = flowerLanguageService.findByOccasionAndEmotion(occasion, emotion);

        // then
        assertThat(flowers).isNotEmpty();
        assertThat(flowers).allMatch(dto -> dto.occasion().equals("CONFESSION"));
        assertThat(flowers).allMatch(dto -> dto.emotion().equals("LOVE"));
    }
}
