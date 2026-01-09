package com.flower.inventory.event;

import com.flower.common.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class InventoryEventListener {

    /**
     * 주문 완료 이벤트 처리
     * 트랜잭션 커밋 후 비동기로 실행됨
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("주문 완료 이벤트 수신: 주문ID={}, 상품={}, 수량={}",
                event.getOrderId(), event.getItem(), event.getQuantity());

        decreaseStock(event.getItem(), event.getQuantity());
    }

    private void decreaseStock(String item, int quantity) {
        log.info("재고 차감 실행: 상품={}, 수량={}", item, quantity);
        // 실제 재고 차감 로직 구현 (ProductService 호출 등)
    }
}
