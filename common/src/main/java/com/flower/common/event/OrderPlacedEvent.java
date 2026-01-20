package com.flower.common.event;

import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

/**
 * 주문 완료 이벤트
 */
@Getter
public class OrderPlacedEvent extends DomainEvent {

    private final String orderNumber; // Business Key (e.g. ORD-1234)
    private final Long internalOrderId;       // Internal DB ID
    private final String itemSummary;
    private final int totalQuantity;
    private final BigDecimal totalAmount;
    
    private final List<OrderItemInfo> items;
    private final DeliveryInfo deliveryInfo;
    private final boolean isDirectOrder;

    public OrderPlacedEvent(String orderNumber, Long internalOrderId, String itemSummary, int totalQuantity, 
                           BigDecimal totalAmount, List<OrderItemInfo> items, DeliveryInfo deliveryInfo, boolean isDirectOrder) {
        super(orderNumber);
        this.orderNumber = orderNumber;
        this.internalOrderId = internalOrderId;
        this.itemSummary = itemSummary;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.items = items;
        this.deliveryInfo = deliveryInfo;
        this.isDirectOrder = isDirectOrder;
    }
    
    public OrderPlacedEvent(String orderNumber, Long internalOrderId, String itemSummary, int totalQuantity, 
            BigDecimal totalAmount, List<OrderItemInfo> items, DeliveryInfo deliveryInfo) {
        this(orderNumber, internalOrderId, itemSummary, totalQuantity, totalAmount, items, deliveryInfo, false);
    }
    
    // Legacy constructor for backward compatibility
    public OrderPlacedEvent(String orderId, String itemSummary, int totalQuantity, BigDecimal totalAmount) {
        this(orderId, null, itemSummary, totalQuantity, totalAmount, null, null);
    }
    
    // 레거시 지원 (삭제 예정)
    public String getOrderId() { return orderNumber; }
    public String getItem() { return itemSummary; }
    public int getQuantity() { return totalQuantity; }
    public double getPrice() { return totalAmount.doubleValue(); }
    
    @Getter
    public static class OrderItemInfo {
        private final Long productId;
        private final String productName;
        private final int quantity;
        private final BigDecimal price;
        
        public OrderItemInfo(Long productId, String productName, int quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }
    }
    
    @Getter
    public static class DeliveryInfo {
        private final String receiverName;
        private final String phone;
        private final String address;
        private final String note;
        
        public DeliveryInfo(String receiverName, String phone, String address, String note) {
            this.receiverName = receiverName;
            this.phone = phone;
            this.address = address;
            this.note = note;
        }
    }
}
