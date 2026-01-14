package com.flower.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "주문 정보")
public record OrderDto(
    Long id,
    String orderNumber,
    BigDecimal totalAmount,
    String status,
    String statusDescription,
    LocalDateTime createdAt,
    String itemSummary
) {}
