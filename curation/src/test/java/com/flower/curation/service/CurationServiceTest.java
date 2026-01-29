package com.flower.curation.service;

import com.flower.curation.dto.CurationRequest;
import com.flower.curation.dto.CurationResult;
import com.flower.curation.enums.Vibe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CurationServiceTest {

    @Autowired
    private CurationService curationService;

    @Test
    @DisplayName("연인 + 고백 + LOVELY + 5만원 = 빨간 장미 계열 추천")
    void should_recommendRoses_when_confessionForLover() {
        // given
        CurationRequest request = new CurationRequest(
                "연인",
                List.of("고백"),
                Vibe.LOVELY,
                new BigDecimal("50000"),
                null
        );

        // when
        CurationResult result = curationService.recommendFlowers(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.bestSeller()).isNotEmpty();
        assertThat(result.storytelling()).isNotEmpty();
        assertThat(result.smartChoice()).isNotEmpty();
        assertThat(result.flowerLanguages()).isNotEmpty();
        
        assertThat(result.flowerLanguages())
                .extracting("flowerName")
                .contains("빨간 장미");
    }

    @Test
    @DisplayName("부모님 + 감사 + NATURAL + 3만원 = 카네이션 포함 추천")
    void should_recommendCarnations_when_gratitudeForParents() {
        // given
        CurationRequest request = new CurationRequest(
                "부모님",
                List.of("감사"),
                Vibe.NATURAL,
                new BigDecimal("30000"),
                null
        );

        // when
        CurationResult result = curationService.recommendFlowers(request);

        // then
        assertThat(result.flowerLanguages())
                .extracting("flowerName")
                .contains("카네이션");
        
        assertThat(result.recommendationReason()).isNotBlank();
    }

    @Test
    @DisplayName("친구 + 생일 + VIVID + 10만원 = 화려한 꽃 추천")
    void should_recommendVividFlowers_when_birthdayForFriend() {
        // given
        CurationRequest request = new CurationRequest(
                "친구",
                List.of("생일"),
                Vibe.VIVID,
                new BigDecimal("100000"),
                null
        );

        // when
        CurationResult result = curationService.recommendFlowers(request);

        // then
        assertThat(result.bestSeller()).hasSizeLessThanOrEqualTo(3);
        assertThat(result.storytelling()).hasSizeLessThanOrEqualTo(3);
        assertThat(result.smartChoice()).hasSizeLessThanOrEqualTo(3);
    }

    @Test
    @DisplayName("다중 상황(생일+감사) 매칭 가능")
    void should_matchMultipleOccasions() {
        // given
        CurationRequest request = new CurationRequest(
                "동료",
                List.of("생일", "감사"),
                Vibe.CHIC,
                new BigDecimal("50000"),
                null
        );

        // when
        CurationResult result = curationService.recommendFlowers(request);

        // then
        assertThat(result.flowerLanguages().size()).isGreaterThan(1);
    }

    @Test
    @DisplayName("제철 꽃이 우선 추천되어야 함")
    void should_prioritizeSeasonalFlowers() {
        // given
        CurationRequest request = new CurationRequest(
                "연인",
                List.of("기념일"),
                Vibe.LOVELY,
                new BigDecimal("50000"),
                null
        );

        // when
        CurationResult result = curationService.recommendFlowers(request);

        // then
        assertThat(result.recommendationReason())
                .containsAnyOf("제철", "계절", "시즌");
    }

    @Test
    @DisplayName("추천 이유가 생성되어야 함")
    void should_generateRecommendationReason() {
        // given
        CurationRequest request = new CurationRequest(
                "연인",
                List.of("고백"),
                Vibe.LOVELY,
                new BigDecimal("50000"),
                null
        );

        // when
        CurationResult result = curationService.recommendFlowers(request);

        // then
        assertThat(result.recommendationReason())
                .contains("연인", "고백");
    }
}
