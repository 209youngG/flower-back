package com.flower.batch.job;

import com.flower.product.repository.ProductRepository;
import com.flower.review.dto.ProductRatingStatDto;
import com.flower.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductRatingSyncJob {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    /**
     * 매일 새벽 3시에 실행되어 상품 평점을 동기화
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void syncProductRatings() {
        log.info("상품 평점 동기화 배치 시작");
        
        List<ProductRatingStatDto> stats = reviewRepository.countReviewStatsByProduct();
        log.info("집계된 상품 수: {}", stats.size());

        for (ProductRatingStatDto stat : stats) {
            productRepository.findById(stat.productId()).ifPresentOrElse(
                product -> {
                    product.updateReviewStats(stat.reviewCount(), stat.totalRating());
                    // JPA Dirty Checking으로 자동 저장
                },
                () -> log.warn("집계된 상품 ID가 존재하지 않음: {}", stat.productId())
            );
        }
        
        log.info("상품 평점 동기화 배치 완료");
    }
}
