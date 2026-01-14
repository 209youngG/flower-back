package com.flower.product.dto;

import java.math.BigDecimal;

public record ProductDto(
    Long id,
    String name,
    BigDecimal price,
    Integer stockQuantity,
    String thumbnailUrl,
    boolean isActive,
    boolean isAvailableToday
) {}
