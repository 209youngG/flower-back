package com.flower.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("대기"),
    PAID("결제 완료"),
    PROCESSING("상품 준비중"),
    SHIPPED("배송중"),
    DELIVERED("배송 완료"),
    CANCELLED("주문 취소");

    private final String description;
}
