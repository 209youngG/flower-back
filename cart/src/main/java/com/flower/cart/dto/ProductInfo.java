package com.flower.cart.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductInfo {
    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal effectivePrice;
    private int stockQuantity;
    private boolean isActive;
}
