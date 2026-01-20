package com.flower.payment.port.out;

import java.math.BigDecimal;

public interface PaymentGatewayPort {
    boolean processPayment(String orderNumber, BigDecimal amount, String paymentKey, String paymentType);
    boolean cancelPayment(String orderNumber, String reason);
}
