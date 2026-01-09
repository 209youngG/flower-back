package com.flower.common.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 주문 완료 이벤트 테스트
 */
@DisplayName("주문 완료 이벤트 테스트")
class OrderPlacedEventTest {

    @Test
    @DisplayName("유효한 파라미터로 이벤트가 생성되어야 한다")
    void shouldCreateOrderPlacedEvent() {
        // 준비
        String orderId = "ORD-123";
        String item = "Laptop";
        int quantity = 2;
        BigDecimal price = new BigDecimal("1500.00");

        // 실행
        OrderPlacedEvent event = new OrderPlacedEvent(orderId, item, quantity, price);

        // 검증
        assertNotNull(event.getOrderId());
        assertEquals(item, event.getItem());
        assertEquals(quantity, event.getQuantity());
        assertEquals(0, Double.compare(price.doubleValue(), event.getPrice()));
        assertEquals("OrderPlacedEvent", event.getEventType());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("각 이벤트마다 고유한 주문 ID가 생성되어야 한다")
    void shouldGenerateUniqueOrderId() {
        // 준비
        OrderPlacedEvent event1 = new OrderPlacedEvent("ORD-1", "Item1", 1, new BigDecimal("100.0"));
        OrderPlacedEvent event2 = new OrderPlacedEvent("ORD-2", "Item2", 2, new BigDecimal("200.0"));

        // 실행 & 검증
        assertNotEquals(event1.getOrderId(), event2.getOrderId());
    }

    @Test
    @DisplayName("올바른 이벤트 타입이 설정되어야 한다")
    void shouldSetCorrectEventType() {
        // 준비
        OrderPlacedEvent event = new OrderPlacedEvent("ORD-TEST", "TestItem", 1, new BigDecimal("100.0"));

        // 실행 & 검증
        assertEquals("OrderPlacedEvent", event.getEventType());
    }

    @Test
    @DisplayName("발생 시간이 설정되어야 한다")
    void shouldSetOccurredOnTimestamp() {
        // 준비
        LocalDateTime before = LocalDateTime.now();
        OrderPlacedEvent event = new OrderPlacedEvent("ORD-TEST", "TestItem", 1, new BigDecimal("100.0"));
        LocalDateTime after = LocalDateTime.now();

        // 실행 & 검증
        assertNotNull(event.getOccurredOn());
        assertTrue(!event.getOccurredOn().isBefore(before));
        assertTrue(!event.getOccurredOn().isAfter(after));
    }
}
