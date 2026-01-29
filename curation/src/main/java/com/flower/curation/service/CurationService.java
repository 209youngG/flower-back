package com.flower.curation.service;

import com.flower.curation.dto.CurationRequest;
import com.flower.curation.dto.CurationResult;
import com.flower.curation.dto.FlowerLanguageDto;
import com.flower.curation.enums.Occasion;
import com.flower.product.domain.Product;
import com.flower.product.dto.ProductDto;
import com.flower.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationService {

    private final FlowerLanguageService flowerLanguageService;
    private final SeasonalityService seasonalityService;
    private final ProductRepository productRepository;

    /**
     * Who + Why + Vibe + Budget를 기반으로 최적의 꽃 추천
     */
    public CurationResult recommendFlowers(CurationRequest request) {
        log.info("꽃 추천 요청 - who: {}, why: {}, vibe: {}, budget: {}",
                request.who(), request.why(), request.vibe(), request.budget());

        // 1. 상황별 꽃말 조회
        List<String> recommendedFlowers = getRecommendedFlowerNames(request.why());
        log.debug("추천 꽃 목록: {}", recommendedFlowers);

        // 2. 제철 꽃 조회
        List<String> seasonalFlowers = seasonalityService.getCurrentSeasonalFlowers();
        log.debug("제철 꽃 목록: {}", seasonalFlowers);

        // 3. 예산 내 재고 있는 활성 상품 검색
        List<Product> availableProducts = searchAvailableProducts(request.budget());

        // 4. 추천 꽃 이름이 포함된 상품 필터링
        List<Product> matchedProducts = filterByFlowerNames(availableProducts, recommendedFlowers);

        // 5. 카테고리별 분류
        List<ProductDto> bestSeller = getBestSellers(matchedProducts);
        List<ProductDto> storytelling = getStorytelling(matchedProducts, seasonalFlowers, request);
        List<ProductDto> smartChoice = getSmartChoice(matchedProducts);

        // 6. 꽃말 정보 조회
        List<FlowerLanguageDto> flowerLanguages = getFlowerLanguages(request.why());

        // 7. 추천 이유 생성
        String recommendationReason = generateRecommendationReason(request, seasonalFlowers, recommendedFlowers);

        return new CurationResult(
                bestSeller,
                storytelling,
                smartChoice,
                flowerLanguages,
                recommendationReason
        );
    }

    /**
     * 상황(why)별로 추천 꽃 이름 추출
     */
    private List<String> getRecommendedFlowerNames(List<String> whyList) {
        return whyList.stream()
                .map(String::toUpperCase)
                .map(this::parseOccasion)
                .filter(occasion -> occasion != null)
                .flatMap(occasion -> flowerLanguageService.findByOccasion(occasion).stream())
                .map(FlowerLanguageDto::flowerName)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * String을 Occasion Enum으로 변환 (안전)
     */
    private Occasion parseOccasion(String why) {
        try {
            return Occasion.valueOf(why);
        } catch (IllegalArgumentException e) {
            log.warn("알 수 없는 상황: {}", why);
            return null;
        }
    }

    /**
     * 예산 내 활성화된 재고 있는 상품 검색
     */
    private List<Product> searchAvailableProducts(BigDecimal budget) {
        List<Product> allProducts = productRepository.findAvailableProducts();

        return allProducts.stream()
                .filter(p -> p.getPrice().compareTo(budget) <= 0)
                .collect(Collectors.toList());
    }

    /**
     * 상품명에 추천 꽃 이름이 포함된 것만 필터링
     */
    private List<Product> filterByFlowerNames(List<Product> products, List<String> flowerNames) {
        if (flowerNames.isEmpty()) {
            return products; // 매칭된 꽃이 없으면 전체 상품 반환
        }

        return products.stream()
                .filter(product -> flowerNames.stream()
                        .anyMatch(flowerName -> product.getName().contains(flowerName)))
                .collect(Collectors.toList());
    }

    /**
     * Best Seller: 리뷰 많은 순 Top 3
     */
    private List<ProductDto> getBestSellers(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparing(Product::getReviewCount).reversed())
                .limit(3)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Storytelling: 꽃말 매칭도 + 제철 여부 점수 기반 Top 3
     */
    private List<ProductDto> getStorytelling(List<Product> products, List<String> seasonalFlowers, CurationRequest request) {
        return products.stream()
                .sorted(Comparator.comparing((Product p) -> calculateScore(p, seasonalFlowers, request)).reversed())
                .limit(3)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Smart Choice: 가성비 (평점 / 가격) 높은 순 Top 3
     */
    private List<ProductDto> getSmartChoice(List<Product> products) {
        return products.stream()
                .filter(p -> p.getAverageRating() > 0) // 평점이 있는 상품만
                .sorted((a, b) -> {
                    double scoreA = a.getAverageRating() / a.getPrice().doubleValue();
                    double scoreB = b.getAverageRating() / b.getPrice().doubleValue();
                    return Double.compare(scoreB, scoreA);
                })
                .limit(3)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 점수 계산 알고리즘 (가중치 기반)
     */
    private Double calculateScore(Product product, List<String> seasonalFlowers, CurationRequest request) {
        double score = 0.0;

        // 1. 제철 꽃이면 +10점
        boolean isSeasonal = seasonalFlowers.stream()
                .anyMatch(flower -> product.getName().contains(flower));
        if (isSeasonal) {
            score += 10.0;
        }

        // 2. 평점 가중치 (×5)
        score += product.getAverageRating() * 5;

        // 3. 리뷰 수 가중치 (log(n+1) × 2)
        score += Math.log(product.getReviewCount() + 1) * 2;

        // 4. 예산 활용도 (70~100% 활용 시 +5점)
        double budgetRatio = product.getPrice().doubleValue() / request.budget().doubleValue();
        if (budgetRatio >= 0.7 && budgetRatio <= 1.0) {
            score += 5.0;
        }

        return score;
    }

    /**
     * 꽃말 정보 조회
     */
    private List<FlowerLanguageDto> getFlowerLanguages(List<String> whyList) {
        return whyList.stream()
                .map(String::toUpperCase)
                .map(this::parseOccasion)
                .filter(occasion -> occasion != null)
                .flatMap(occasion -> flowerLanguageService.findByOccasion(occasion).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 추천 이유 생성
     */
    private String generateRecommendationReason(CurationRequest request, List<String> seasonalFlowers, List<String> recommendedFlowers) {
        StringBuilder reason = new StringBuilder();

        reason.append(request.who()).append("에게 ");
        reason.append(String.join(", ", request.why())).append(" 상황에 어울리는 ");

        if (!seasonalFlowers.isEmpty()) {
            reason.append("제철 꽃을 포함한 ");
        }

        reason.append(request.vibe().getDescription()).append(" 분위기의 꽃을 추천드립니다.");

        if (!recommendedFlowers.isEmpty()) {
            reason.append(" 특히 ").append(String.join(", ", recommendedFlowers)).append("이(가) 잘 어울립니다.");
        }

        return reason.toString();
    }

    /**
     * Product -> ProductDto 변환
     */
    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getStoreId(),
                product.getName(),
                product.getPrice(),
                product.getDiscountPrice(),
                product.getStockQuantity(),
                product.getDescription(),
                product.getThumbnailUrl(),
                product.getIsActive(),
                product.getIsAvailableToday(),
                new ArrayList<>(), // options는 필요시 매핑
                product.getCategory(),
                product.getDeliveryType(),
                product.getReviewCount(),
                product.getTotalRating(),
                product.getAverageRating()
        );
    }
}
