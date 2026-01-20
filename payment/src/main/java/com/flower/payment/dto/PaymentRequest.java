package com.flower.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(
    String orderNumber,
    Long orderId,
    BigDecimal amount,
    String paymentKey,
    String paymentType,
    String orderName
) {}
