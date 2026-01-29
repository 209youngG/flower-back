package com.flower.curation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SeasonalityService 테스트 (TDD Red-Green-Refactor)
 * 
 * 목표: 월별 제철 꽃 데이터베이스를 구축하고 계절에 맞는 꽃만 추천
 */
@SpringBootTest
class SeasonalityServiceTest {

    @Autowired
    private SeasonalityService seasonalityService;

    @Test
    @DisplayName("1월에는 동백, 수선화가 조회되어야 한다")
    void should_returnJanuaryFlowers_when_monthIsJanuary() {
        // given
        int month = 1;

        // when
        List<String> flowers = seasonalityService.getSeasonalFlowers(month);

        // then
        assertThat(flowers).isNotEmpty();
        assertThat(flowers).contains("동백", "수선화");
    }

    @Test
    @DisplayName("3월에는 벚꽃, 목련이 조회되어야 한다")
    void should_returnMarchFlowers_when_monthIsMarch() {
        // given
        int month = 3;

        // when
        List<String> flowers = seasonalityService.getSeasonalFlowers(month);

        // then
        assertThat(flowers).contains("벚꽃", "목련");
    }

    @Test
    @DisplayName("6월에는 수국, 장미가 조회되어야 한다")
    void should_returnJuneFlowers_when_monthIsJune() {
        // given
        int month = 6;

        // when
        List<String> flowers = seasonalityService.getSeasonalFlowers(month);

        // then
        assertThat(flowers).contains("수국", "장미");
    }

    @Test
    @DisplayName("현재 월의 제철 꽃을 조회할 수 있다")
    void should_returnCurrentSeasonalFlowers() {
        // when
        List<String> flowers = seasonalityService.getCurrentSeasonalFlowers();

        // then
        assertThat(flowers).isNotEmpty();
    }

    @Test
    @DisplayName("성수기 꽃만 필터링할 수 있다")
    void should_returnPeakSeasonFlowers_when_peakSeasonIsTrue() {
        // given
        int month = 1;

        // when
        List<String> flowers = seasonalityService.getPeakSeasonFlowers(month);

        // then
        assertThat(flowers).isNotEmpty();
        // 1월 성수기 꽃: 동백
        assertThat(flowers).contains("동백");
    }

    @Test
    @DisplayName("잘못된 월(0) 입력 시 빈 리스트 반환")
    void should_returnEmptyList_when_monthIsInvalid() {
        // given
        int invalidMonth = 0;

        // when
        List<String> flowers = seasonalityService.getSeasonalFlowers(invalidMonth);

        // then
        assertThat(flowers).isEmpty();
    }

    @Test
    @DisplayName("잘못된 월(13) 입력 시 빈 리스트 반환")
    void should_returnEmptyList_when_monthIsOverTwelve() {
        // given
        int invalidMonth = 13;

        // when
        List<String> flowers = seasonalityService.getSeasonalFlowers(invalidMonth);

        // then
        assertThat(flowers).isEmpty();
    }
}
