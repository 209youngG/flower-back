package com.flower.order.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    @DisplayName("주문 총액이 올바르게 계산되어야 한다")
    void shouldCalculateTotalAmountCorrectly() {
        // 준비
        Order order = Order.builder()
                .orderNumber("ORD-001")
                .build();

        OrderItem item1 = OrderItem.builder()
                .productId(1L)
                .productName("Rose")
                .quantity(2)
                .unitPrice(new BigDecimal("10000"))
                .build();
        
        OrderItem item2 = OrderItem.builder()
                .productId(2L)
                .productName("Tulip")
                .quantity(1)
                .unitPrice(new BigDecimal("15000"))
                .build();

        order.addItem(item1);
        order.addItem(item2);

        // 실행
        order.calculateTotal();

        // 검증
        // 상품1: 10000 * 2 = 20000
        // 상품2: 15000 * 1 = 15000
        // 합계: 35000
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("35000"));
    }
}
