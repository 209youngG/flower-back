package com.flower.product.event;

import com.flower.common.event.ReviewCreatedEvent;
import com.flower.common.event.ReviewDeletedEvent;
import com.flower.common.event.ReviewUpdatedEvent;
import com.flower.product.domain.Product;
import com.flower.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReviewCreated(ReviewCreatedEvent event) {
        log.info("리뷰 생성 이벤트 수신 - 상품 평점 갱신 시작: ProductId={}, Rating={}", event.getProductId(), event.getRating());
        updateProduct(event.getProductId(), product -> product.addReviewRating(event.getRating()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReviewUpdated(ReviewUpdatedEvent event) {
        log.info("리뷰 수정 이벤트 수신 - 평점 변경: ProductId={}, Old={}, New={}", event.getProductId(), event.getOldRating(), event.getNewRating());
        updateProduct(event.getProductId(), product -> product.updateReviewRating(event.getOldRating(), event.getNewRating()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReviewDeleted(ReviewDeletedEvent event) {
        log.info("리뷰 삭제 이벤트 수신 - 평점 차감: ProductId={}, Rating={}", event.getProductId(), event.getRating());
        updateProduct(event.getProductId(), product -> product.removeReviewRating(event.getRating()));
    }

    private void updateProduct(Long productId, java.util.function.Consumer<Product> updater) {
        productRepository.findById(productId).ifPresentOrElse(
                product -> {
                    updater.accept(product);
                    log.info("상품 통계 업데이트 완료: ID={}, Count={}, Avg={}", 
                            product.getId(), product.getReviewCount(), product.getAverageRating());
                },
                () -> log.error("상품을 찾을 수 없어 평점 갱신 실패: ProductId={}", productId)
        );
    }
}
