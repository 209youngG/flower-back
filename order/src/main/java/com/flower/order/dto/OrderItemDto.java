package com.flower.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private List<OrderItemOptionDto> options;
}
