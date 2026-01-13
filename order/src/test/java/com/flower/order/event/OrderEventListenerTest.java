package com.flower.order.event;

import com.flower.common.event.InventoryDeductionFailedEvent;
import com.flower.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderEventListener Tests")
class OrderEventListenerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderEventListener orderEventListener;

    @Test
    @DisplayName("Should cancel order when inventory deduction fails")
    void shouldCancelOrderWhenInventoryDeductionFails() {
        // Given
        InventoryDeductionFailedEvent event = new InventoryDeductionFailedEvent("ORD-123", "Out of stock");

        // When
        orderEventListener.handleInventoryDeductionFailed(event);

        // Then
        verify(orderService).cancelOrder("ORD-123", "Out of stock");
    }
}
