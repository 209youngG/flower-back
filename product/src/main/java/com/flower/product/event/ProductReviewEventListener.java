package com.flower.product.event;

import com.flower.common.event.ReviewCreatedEvent;
import com.flower.product.domain.Product;
import com.flower.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductReviewEventListener {

    private final ProductRepository productRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void handleReviewCreated(ReviewCreatedEvent event) {
        log.info("리뷰 생성 이벤트 수신 - 상품 평점 갱신 시작: ProductId={}, Rating={}", event.getProductId(), event.getRating());

        productRepository.findById(event.getProductId()).ifPresentOrElse(
                product -> {
                    product.addReviewRating(event.getRating());
                    // Dirty checking으로 자동 저장되지만, 명시적으로 save 호출해도 무방
                    log.info("상품 평점 갱신 완료: ProductId={}, NewCount={}, NewAvg={}", 
                            product.getId(), product.getReviewCount(), product.getAverageRating());
                },
                () -> log.error("상품을 찾을 수 없어 평점 갱신 실패: ProductId={}", event.getProductId())
        );
    }
}
