package com.flower.product.dto;

import java.math.BigDecimal;

public record ProductOptionDto(
    Long id,
    String name,
    String optionValue,
    BigDecimal priceAdjustment
) {}
