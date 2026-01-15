package com.flower.product.dto;

import com.flower.product.domain.ProductAddon;
import java.math.BigDecimal;

public record ProductAddonDto(
    Long id,
    String name,
    BigDecimal price,
    String imageUrl,
    ProductAddon.Category category,
    String description
) {}
