package com.flower.api.dto;

public record PaymentRequest(
    Long orderId,
    String paymentMethod // CARD, BANK_TRANSFER
) {}
