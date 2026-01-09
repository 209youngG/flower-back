package com.flower.common.event;

import lombok.Getter;
import java.math.BigDecimal;

/**
 * 주문 완료 이벤트
 */
@Getter
public class OrderPlacedEvent extends DomainEvent {

    private final String orderId;
    private final String itemSummary;
    private final int totalQuantity;
    private final BigDecimal totalAmount;

    public OrderPlacedEvent(String orderId, String itemSummary, int totalQuantity, BigDecimal totalAmount) {
        super(orderId);
        this.orderId = orderId;
        this.itemSummary = itemSummary;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }
    
    // 레거시 지원 메서드 (다른 모듈에서 필요한 경우 사용, 가급적 consumers 업데이트 권장)
    public String getItem() { return itemSummary; }
    public int getQuantity() { return totalQuantity; }
    public double getPrice() { return totalAmount.doubleValue(); }
}
