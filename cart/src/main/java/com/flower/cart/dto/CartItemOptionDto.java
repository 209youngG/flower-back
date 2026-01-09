package com.flower.cart.dto;

import java.math.BigDecimal;

public record CartItemOptionDto(
    Long productOptionId,
    Long productAddonId,
    BigDecimal priceAdjustment
) {}
