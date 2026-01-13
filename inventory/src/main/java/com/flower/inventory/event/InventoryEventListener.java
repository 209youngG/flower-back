package com.flower.inventory.event;

import com.flower.common.event.InventoryDeductionFailedEvent;
import com.flower.common.event.OrderPlacedEvent;
import com.flower.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final ProductService productService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 주문 완료 이벤트 처리
     * 트랜잭션 커밋 후 비동기로 실행됨
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("주문 완료 이벤트 수신: 주문ID={}, 상품={}, 수량={}",
                event.getOrderNumber(), event.getItemSummary(), event.getTotalQuantity());

        if (event.getItems() != null) {
            try {
                for (OrderPlacedEvent.OrderItemInfo item : event.getItems()) {
                    decreaseStock(item.getProductId(), item.getQuantity());
                }
            } catch (Exception e) {
                log.error("재고 차감 실패로 인한 보상 트랜잭션 발동: 주문번호={}, 오류={}", event.getOrderNumber(), e.getMessage());
                eventPublisher.publishEvent(new InventoryDeductionFailedEvent(event.getOrderNumber(), e.getMessage()));
            }
        } else {
            log.warn("주문 상품 상세 정보가 없습니다. 재고 차감을 건너뜁니다. OrderID={}", event.getOrderNumber());
        }
    }

    private void decreaseStock(Long productId, int quantity) {
        log.info("재고 차감 실행: 상품ID={}, 수량={}", productId, quantity);
        productService.decreaseStock(productId, quantity);
    }
}
