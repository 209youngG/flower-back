package com.flower.common.event;

import lombok.Getter;
import java.util.List;

@Getter
public class PaymentCompletedEvent extends DomainEvent {
    private final String orderNumber;
    private final Long orderId;
    private final List<OrderPlacedEvent.OrderItemInfo> items;

    public PaymentCompletedEvent(String orderNumber, Long orderId, List<OrderPlacedEvent.OrderItemInfo> items) {
        super(orderNumber);
        this.orderNumber = orderNumber;
        this.orderId = orderId;
        this.items = items;
    }
}
