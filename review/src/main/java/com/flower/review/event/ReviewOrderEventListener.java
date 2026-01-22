package com.flower.review.event;

import com.flower.common.event.OrderCancelledEvent;
import com.flower.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewOrderEventListener {

    private final ReviewService reviewService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("주문 취소 이벤트 수신 - 리뷰 숨김 처리 시작: OrderId={}, OrderNumber={}", event.getOrderId(), event.getOrderNumber());

        List<Long> orderItemIds = event.getCancelledOrderItemIds();

        if (orderItemIds != null && !orderItemIds.isEmpty()) {
            reviewService.hideReviewsForOrderItems(orderItemIds);
            log.info("주문 취소로 인한 리뷰 숨김 요청 완료: {} 개 상품", orderItemIds.size());
        } else {
            log.warn("주문 취소 이벤트에 상품 ID 목록이 없어 리뷰 숨김 처리를 건너뜁니다.");
        }
    }
}
