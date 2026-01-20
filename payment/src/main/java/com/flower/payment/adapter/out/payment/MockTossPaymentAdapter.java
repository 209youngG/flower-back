package com.flower.payment.adapter.out.payment;

import com.flower.payment.port.out.PaymentGatewayPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class MockTossPaymentAdapter implements PaymentGatewayPort {

    @Override
    public boolean processPayment(String orderNumber, BigDecimal amount, String paymentKey, String paymentType) {
        log.info("[PG-Toss] 결제 승인 요청 - 주문번호: {}, 금액: {}, Key: {}", orderNumber, amount, paymentKey);
        
        // Mocking: "FAIL" 키워드가 포함되면 결제 실패 처리
        if (paymentKey.contains("FAIL")) {
            log.warn("[PG-Toss] 결제 승인 실패 (Mock)");
            return false;
        }
        
        log.info("[PG-Toss] 결제 승인 성공 (Mock)");
        return true;
    }

    @Override
    public boolean cancelPayment(String orderNumber, String reason) {
        log.info("[PG-Toss] 결제 취소 요청 - 주문번호: {}, 사유: {}", orderNumber, reason);
        log.info("[PG-Toss] 결제 취소 성공 (Mock)");
        return true;
    }
}
