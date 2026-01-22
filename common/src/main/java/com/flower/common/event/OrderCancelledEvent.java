package com.flower.common.event;

import lombok.Getter;
import java.util.List;

@Getter
public class OrderCancelledEvent extends DomainEvent {
    private final String orderNumber;
    private final Long orderId; // 추가된 필드
    private final String reason;
    private final Long memberId;
    private final List<OrderPlacedEvent.OrderItemInfo> items;
    private final List<Long> cancelledOrderItemIds;

    public OrderCancelledEvent(String orderNumber, Long orderId, String reason, Long memberId, 
                             List<OrderPlacedEvent.OrderItemInfo> items, List<Long> cancelledOrderItemIds) {
        super(orderNumber);
        this.orderNumber = orderNumber;
        this.orderId = orderId;
        this.reason = reason;
        this.memberId = memberId;
        this.items = items;
        this.cancelledOrderItemIds = cancelledOrderItemIds;
    }
}
