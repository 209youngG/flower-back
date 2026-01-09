package com.flower.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemOptionDto {
    private Long productOptionId;
    private Long productAddonId;
    private String optionName;
    private BigDecimal price;
}
