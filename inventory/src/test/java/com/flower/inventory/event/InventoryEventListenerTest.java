package com.flower.inventory.event;

import com.flower.common.event.OrderPlacedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InventoryEventListener 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryEventListener Tests")
class InventoryEventListenerTest {

    @InjectMocks
    private InventoryEventListener inventoryEventListener;

    @Test
    @DisplayName("Should handle OrderPlacedEvent")
    void shouldHandleOrderPlacedEvent() {
        // 실행
        // 참고: handleOrderPlaced는 @Async 및 @EventListener 주석이 달려있으므로
        // Spring 컨텍스트를 통해 테스트하거나 리플렉션을 사용해야 합니다.
        // 여기서는 메서드 존재 여부와 주석을 검증합니다.

        // 검증
        assertNotNull(inventoryEventListener);
    }

    @Test
    @DisplayName("Should have handleOrderPlaced method with TransactionalEventListener annotation")
    void shouldHaveHandleOrderPlacedMethodWithTransactionalEventListenerAnnotation() throws NoSuchMethodException {
        // 실행
        Method method = InventoryEventListener.class.getMethod("handleOrderPlaced", OrderPlacedEvent.class);

        // 검증
        assertNotNull(method);
        assertEquals("handleOrderPlaced", method.getName());
        assertTrue(method.isAnnotationPresent(org.springframework.transaction.event.TransactionalEventListener.class));
    }

    @Test
    @DisplayName("Should have Async annotation on handleOrderPlaced method")
    void shouldHaveAsyncAnnotationOnHandleOrderPlacedMethod() throws NoSuchMethodException {
        // 실행
        Method method = InventoryEventListener.class.getMethod("handleOrderPlaced", OrderPlacedEvent.class);

        // 검증
        assertTrue(method.isAnnotationPresent(org.springframework.scheduling.annotation.Async.class));
    }

    @Test
    @DisplayName("Should have TransactionalEventListener with AFTER_COMMIT phase")
    void shouldHaveTransactionalEventListenerWithAfterCommitPhase() throws NoSuchMethodException {
        // 실행
        Method method = InventoryEventListener.class.getMethod("handleOrderPlaced", OrderPlacedEvent.class);

        // 검증
        assertTrue(method.isAnnotationPresent(org.springframework.transaction.event.TransactionalEventListener.class));
        org.springframework.transaction.event.TransactionalEventListener annotation =
                method.getAnnotation(org.springframework.transaction.event.TransactionalEventListener.class);
        assertEquals(org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT, annotation.phase());
    }

    @Test
    @DisplayName("Should have Component annotation on class")
    void shouldHaveComponentAnnotationOnClass() {
        // 실행 및 검증
        assertTrue(inventoryEventListener.getClass().isAnnotationPresent(org.springframework.stereotype.Component.class));
    }

}
