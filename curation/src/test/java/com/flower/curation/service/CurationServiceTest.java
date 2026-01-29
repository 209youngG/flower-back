package com.flower.curation.service;

import com.flower.curation.domain.FlowerLanguage;
import com.flower.curation.domain.SeasonalFlower;
import com.flower.curation.dto.CurationRequest;
import com.flower.curation.dto.CurationResult;
import com.flower.curation.dto.FlowerLanguageDto;
import com.flower.curation.enums.Emotion;
import com.flower.curation.enums.Occasion;
import com.flower.curation.enums.Vibe;
import com.flower.curation.repository.FlowerLanguageRepository;
import com.flower.curation.repository.SeasonalFlowerRepository;
import com.flower.product.domain.Product;
import com.flower.product.domain.ProductCategory;
import com.flower.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CurationServiceTest {

    @Mock
    private FlowerLanguageRepository flowerLanguageRepository;

    @Mock
    private SeasonalFlowerRepository seasonalFlowerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FlowerLanguageService flowerLanguageService;

    @InjectMocks
    private SeasonalityService seasonalityService;

    @InjectMocks
    private CurationService curationService;

    @BeforeEach
    void setUp() {
        // FlowerLanguageService와 SeasonalityService를 CurationService에 수동 주입
        curationService = new CurationService(
                flowerLanguageService,
                seasonalityService,
                productRepository
        );

        // Mock 데이터 설정
        setupFlowerLanguageMocks();
        setupSeasonalFlowerMocks();
        setupProductMocks();
    }

    private void setupFlowerLanguageMocks() {
        // 고백 -> 빨간 장미
        when(flowerLanguageRepository.findByOccasion(Occasion.CONFESSION))
                .thenReturn(List.of(
                        FlowerLanguage.builder()
                                .flowerName("빨간 장미")
                                .occasion(Occasion.CONFESSION)
                                .meaning("당신을 사랑합니다")
                                .emotion(Emotion.LOVE)
                                .build()
                ));

        // 감사 -> 카네이션
        when(flowerLanguageRepository.findByOccasion(Occasion.GRATITUDE))
                .thenReturn(List.of(
                        FlowerLanguage.builder()
                                .flowerName("카네이션")
                                .occasion(Occasion.GRATITUDE)
                                .meaning("감사합니다")
                                .emotion(Emotion.RESPECT)
                                .build()
                ));

        // 생일 -> 튤립
        when(flowerLanguageRepository.findByOccasion(Occasion.BIRTHDAY))
                .thenReturn(List.of(
                        FlowerLanguage.builder()
                                .flowerName("튤립")
                                .occasion(Occasion.BIRTHDAY)
                                .meaning("축하합니다")
                                .emotion(Emotion.JOY)
                                .build()
                ));

        // 기념일 -> 장미
        when(flowerLanguageRepository.findByOccasion(Occasion.ANNIVERSARY))
                .thenReturn(List.of(
                        FlowerLanguage.builder()
                                .flowerName("장미")
                                .occasion(Occasion.ANNIVERSARY)
                                .meaning("영원한 사랑")
                                .emotion(Emotion.LOVE)
                                .build()
                ));
    }

    private void setupSeasonalFlowerMocks() {
        // 1월(현재 시즌) 제철 꽃
        when(seasonalFlowerRepository.findByMonth(any(Integer.class)))
                .thenReturn(List.of(
                        SeasonalFlower.builder()
                                .month(1)
                                .flowerName("장미")
                                .peakSeason(true)
                                .build(),
                        SeasonalFlower.builder()
                                .month(1)
                                .flowerName("튤립")
                                .peakSeason(false)
                                .build()
                ));
    }

    private void setupProductMocks() {
        // 재고 있는 활성 상품 목록
        when(productRepository.findAvailableProducts())
                .thenReturn(List.of(
                        createProduct(1L, "빨간 장미 꽃다발", new BigDecimal("45000"), 10, 100L, 450L),
                        createProduct(2L, "카네이션 화분", new BigDecimal("25000"), 5, 50L, 225L),
                        createProduct(3L, "튤립 부케", new BigDecimal("35000"), 8, 80L, 360L),
                        createProduct(4L, "장미 바스켓", new BigDecimal("55000"), 7, 70L, 315L),
                        createProduct(5L, "혼합 꽃다발", new BigDecimal("40000"), 9, 90L, 405L)
                ));
    }

    private Product createProduct(Long id, String name, BigDecimal price, int stock, Long reviewCount, Long totalRating) {
        Product product = Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .stockQuantity(stock)
                .isActive(true)
                .isAvailableToday(true)
                .category(ProductCategory.FLOWER_BOUQUET)
                .deliveryType(Product.DeliveryType.QUICK)
                .reviewCount(reviewCount)
                .totalRating(totalRating)
                .build();

        // averageRating 계산
        product.updateReviewStats(reviewCount, totalRating);

        return product;
    }

    @Test
    @DisplayName("연인 + 고백 + LOVELY + 5만원 = 빨간 장미 계열 추천")
    void should_recommendRoses_when_confessionForLover() {
        // given
        CurationRequest request = new CurationRequest(
                "연인",
                List.of("CONFESSION"),
                Vibe.LOVELY,
                new BigDecimal("50000"),
                null
        );

        // when
        CurationResult result = curationService.recommendFlowers(request);

        // then
        assertThat(result).isNotNull();
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
                List.of("GRATITUDE"),
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
                List.of("BIRTHDAY"),
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
                List.of("BIRTHDAY", "GRATITUDE"),
                Vibe.CHIC,
                new BigDecimal("50000"),
                null
        );

        // when
        CurationResult result = curationService.recommendFlowers(request);

        // then
        assertThat(result.flowerLanguages().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("제철 꽃이 우선 추천되어야 함")
    void should_prioritizeSeasonalFlowers() {
        // given
        CurationRequest request = new CurationRequest(
                "연인",
                List.of("ANNIVERSARY"),
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
                List.of("CONFESSION"),
                Vibe.LOVELY,
                new BigDecimal("50000"),
                null
        );

        // when
        CurationResult result = curationService.recommendFlowers(request);

        // then
        assertThat(result.recommendationReason())
                .contains("연인", "CONFESSION");
    }
}

