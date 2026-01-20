package com.flower.payment.event;

import com.flower.common.event.InventoryDeductionFailedEvent;
import com.flower.common.event.OrderCancelledEvent;
import com.flower.order.service.OrderService;
import com.flower.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;
    private final OrderService orderService;

    /**
     * 주문 취소 이벤트 수신 시 결제 취소 처리
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("주문 취소 이벤트 수신 - 결제 취소 시작: 주문번호={}, 주문ID={}", event.getOrderNumber(), event.getOrderId());
        
        try {
            // PG사 취소 요청
            paymentService.cancelPaymentByOrderNumber(event.getOrderNumber(), event.getReason());
            // 내부 상태 변경
            paymentService.cancelPayment(event.getOrderId());
            log.info("결제 취소 완료: 주문ID={}", event.getOrderId());
        } catch (Exception e) {
            log.error("결제 취소 실패: 주문ID={}, 오류={}", event.getOrderId(), e.getMessage());
        }
    }

    /**
     * 재고 차감 실패 시 보상 트랜잭션 (결제 취소 및 주문 취소)
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInventoryDeductionFailed(InventoryDeductionFailedEvent event) {
        log.info("재고 차감 실패 이벤트 수신 - 보상 트랜잭션 시작: 주문번호={}, 사유={}", event.getOrderNumber(), event.getReason());
        
        try {
            orderService.cancelOrder(event.getOrderNumber(), "재고 부족으로 인한 자동 취소: " + event.getReason());
        } catch (Exception e) {
            log.error("보상 트랜잭션 실패: 주문번호={}, 오류={}", event.getOrderNumber(), e.getMessage());
        }
    }
}
