package com.flower.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "주문 상세 정보")
public record OrderDetailDto(
    Long id,
    String orderNumber,
    BigDecimal totalAmount,
    String status,
    String statusDescription,
    LocalDateTime createdAt,
    
    String deliveryName,
    String deliveryPhone,
    String deliveryAddress,
    String deliveryNote,
    
    List<OrderItemDto> items
) {}
