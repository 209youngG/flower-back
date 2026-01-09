package com.flower.order.dto;

import com.flower.order.domain.Order;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "주문 생성 응답 DTO")
public record CreateOrderResponse(
    @Schema(description = "주문 번호", example = "ORD-123456789")
    String orderNumber,
    
    @Schema(description = "총 주문 금액", example = "50000")
    BigDecimal totalAmount,
    
    @Schema(description = "주문 상태", example = "PENDING")
    Order.OrderStatus status,
    
    @Schema(description = "주문 생성 일시")
    LocalDateTime createdAt,

    @Schema(description = "메시지 카드 내용", example = "생일 축하해!")
    String messageCard
) {
    public static CreateOrderResponse from(Order order) {
        return new CreateOrderResponse(
            order.getOrderNumber(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getMessageCard()
        );
    }
}
