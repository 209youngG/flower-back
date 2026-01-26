package com.flower.product.dto;

import java.math.BigDecimal;

import java.util.List;
import com.flower.product.domain.Product.DeliveryType;
import com.flower.product.domain.ProductCategory;

public record ProductDto(
    Long id,
    String name,
    BigDecimal price,
    BigDecimal discountPrice,
    Integer stockQuantity,
    String description,
    String thumbnailUrl,
    boolean isActive,
    boolean isAvailableToday,
    List<ProductOptionDto> options,
    ProductCategory category,
    DeliveryType deliveryType,
    Long reviewCount,
    Long totalRating,
    Double averageRating
) {}
