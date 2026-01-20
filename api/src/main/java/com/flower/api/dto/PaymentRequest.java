package com.flower.api.dto;

import java.math.BigDecimal;

public record PaymentRequest(
    String orderNumber,
    Long orderId,
    BigDecimal amount,
    String paymentKey,
    String paymentMethod // CARD, BANK_TRANSFER
) {}
