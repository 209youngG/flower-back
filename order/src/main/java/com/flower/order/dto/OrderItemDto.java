package com.flower.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private List<OrderItemOptionDto> options;
    private Long orderItemId; 
    private boolean hasReview; 
}
