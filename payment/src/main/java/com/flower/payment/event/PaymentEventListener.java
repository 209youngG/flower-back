package com.flower.payment.event;

import com.flower.common.event.OrderCancelledEvent;
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

    /**
     * 주문 취소 이벤트 수신 시 결제 취소 처리
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("주문 취소 이벤트 수신 - 결제 취소 시작: 주문번호={}, 주문ID={}", event.getOrderNumber(), event.getOrderId());
        
        try {
            paymentService.cancelPayment(event.getOrderId());
            log.info("결제 취소 완료: 주문ID={}", event.getOrderId());
        } catch (Exception e) {
            log.error("결제 취소 실패: 주문ID={}, 오류={}", event.getOrderId(), e.getMessage());
            // 필요한 경우 보상 트랜잭션 실패 이벤트 발행 또는 알림 전송 로직 추가
        }
    }
}
