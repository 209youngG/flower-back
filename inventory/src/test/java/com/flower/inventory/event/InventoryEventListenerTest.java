package com.flower.inventory.event;

import com.flower.common.event.OrderPlacedEvent;
import com.flower.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEventPublisher;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.Mockito.*;

/**
 * InventoryEventListener 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryEventListener Tests")
class InventoryEventListenerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InventoryEventListener inventoryEventListener;

    @Test
    @DisplayName("Should decrease stock when OrderPlacedEvent is received")
    void shouldDecreaseStockWhenOrderPlacedEventIsReceived() {
        // 준비
        OrderPlacedEvent.OrderItemInfo item1 = new OrderPlacedEvent.OrderItemInfo(1L, "Rose", 2, new BigDecimal("10000"));
        OrderPlacedEvent.OrderItemInfo item2 = new OrderPlacedEvent.OrderItemInfo(2L, "Lily", 3, new BigDecimal("8000"));
        
        OrderPlacedEvent event = new OrderPlacedEvent(
                "ORD-001", 1L, "Rose x 2, Lily x 3", 5, new BigDecimal("44000"),
                List.of(item1, item2), null
        );

        // 실행
        inventoryEventListener.handleOrderPlaced(event);

        // 검증
        verify(productService).decreaseStock(1L, 2);
        verify(productService).decreaseStock(2L, 3);
        verify(eventPublisher, never()).publishEvent(any());
    }
    
    @Test
    @DisplayName("Should publish failure event when stock deduction fails")
    void shouldPublishFailureEventWhenStockDeductionFails() {
        // 준비
        OrderPlacedEvent.OrderItemInfo item1 = new OrderPlacedEvent.OrderItemInfo(1L, "Rose", 2, new BigDecimal("10000"));
        OrderPlacedEvent event = new OrderPlacedEvent(
                "ORD-FAIL", 1L, "Rose x 2", 2, new BigDecimal("20000"),
                List.of(item1), null
        );

        doThrow(new RuntimeException("Out of stock")).when(productService).decreaseStock(1L, 2);

        // 실행
        inventoryEventListener.handleOrderPlaced(event);

        // 검증
        verify(productService).decreaseStock(1L, 2);
        verify(eventPublisher).publishEvent(any(com.flower.common.event.InventoryDeductionFailedEvent.class));
    }
    
    @Test
    @DisplayName("Should log warning when items are missing")
    void shouldLogWarningWhenItemsAreMissing() {
        // 준비
        OrderPlacedEvent event = new OrderPlacedEvent(
                "ORD-002", 2L, "Unknown Item", 1, new BigDecimal("10000"),
                null, null
        );

        // 실행
        inventoryEventListener.handleOrderPlaced(event);

        // 검증
        verify(productService, never()).decreaseStock(anyLong(), anyInt());
    }
}
