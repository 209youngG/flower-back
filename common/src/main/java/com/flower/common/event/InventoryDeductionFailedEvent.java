package com.flower.common.event;

import lombok.Getter;

/**
 * 재고 차감 실패 이벤트
 * 보상 트랜잭션(주문 취소)을 위해 사용됨
 */
@Getter
public class InventoryDeductionFailedEvent extends DomainEvent {

    private final String orderNumber;
    private final String reason;

    public InventoryDeductionFailedEvent(String orderNumber, String reason) {
        super(orderNumber);
        this.orderNumber = orderNumber;
        this.reason = reason;
    }
}
