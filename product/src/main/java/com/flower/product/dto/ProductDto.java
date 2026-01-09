package com.flower.product.dto;

import java.math.BigDecimal;

public record ProductDto(
    Long id,
    String name,
    BigDecimal price,
    boolean isActive,
    boolean isAvailableToday
) {}
