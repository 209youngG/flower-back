package com.flower.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartItemDto(
    Long id,
    Long productId,
    int quantity,
    BigDecimal unitPrice,
    List<CartItemOptionDto> options,
    BigDecimal totalPrice
) {}
