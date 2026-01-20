package com.flower.cart.event;

import com.flower.cart.service.CartService;
import com.flower.common.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartEventListener {

    private final CartService cartService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("결제 완료 이벤트 수신 - 장바구니 비우기 시작: 주문ID={}", event.getOrderId());
        
        if (event.isDirectOrder()) {
            log.info("바로 주문 건이므로 장바구니를 유지합니다. OrderID={}", event.getOrderId());
            return;
        }
        
        if (event.getMemberId() != null) {
            String cartKey = "cart-user-" + event.getMemberId();
            cartService.clearCart(cartKey);
            log.info("장바구니 비우기 완료: 회원ID={}, 카트키={}", event.getMemberId(), cartKey);
        } else {
            log.warn("결제 완료 이벤트에 회원 ID가 없어 장바구니를 비울 수 없습니다. 주문ID={}", event.getOrderId());
        }
    }
}
