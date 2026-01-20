package com.flower.order.dto;

import com.flower.order.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 상태 수정 요청")
public record UpdateOrderStatusRequest(
    @Schema(description = "변경할 주문 상태 (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED, COMPLETED)")
    OrderStatus status
) {}
