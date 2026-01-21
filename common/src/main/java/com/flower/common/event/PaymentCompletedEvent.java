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
    private final String deliveryName;
    private final String deliveryPhone;
    private final String deliveryAddress;
    private final String deliveryNote;

    public PaymentCompletedEvent(String orderNumber, Long orderId, Long memberId, List<OrderPlacedEvent.OrderItemInfo> items, boolean isDirectOrder,
                                 String deliveryName, String deliveryPhone, String deliveryAddress, String deliveryNote) {
        super(orderNumber);
        this.orderNumber = orderNumber;
        this.orderId = orderId;
        this.memberId = memberId;
        this.items = items;
        this.isDirectOrder = isDirectOrder;
        this.deliveryName = deliveryName;
        this.deliveryPhone = deliveryPhone;
        this.deliveryAddress = deliveryAddress;
        this.deliveryNote = deliveryNote;
    }
}
