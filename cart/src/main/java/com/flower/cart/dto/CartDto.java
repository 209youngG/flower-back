package com.flower.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
    String cartKey,
    Long memberId,
    List<CartItemDto> items,
    int totalQuantity,
    BigDecimal totalPrice
) {
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
