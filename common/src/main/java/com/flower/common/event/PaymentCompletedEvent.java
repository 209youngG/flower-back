package com.flower.common.event;

import lombok.Getter;
import java.util.List;

@Getter
public class PaymentCompletedEvent extends DomainEvent {
    private final String orderNumber;
    private final Long orderId;
    private final Long memberId;
    private final List<OrderPlacedEvent.OrderItemInfo> items;
    private final boolean isDirectOrder;

    public PaymentCompletedEvent(String orderNumber, Long orderId, Long memberId, List<OrderPlacedEvent.OrderItemInfo> items, boolean isDirectOrder) {
        super(orderNumber);
        this.orderNumber = orderNumber;
        this.orderId = orderId;
        this.memberId = memberId;
        this.items = items;
        this.isDirectOrder = isDirectOrder;
    }
}
