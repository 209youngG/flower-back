package com.flower.common.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EntityNotFoundException 테스트
 */
@DisplayName("EntityNotFoundException Tests")
class EntityNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        // 준비
        String message = "Entity not found with id: 123";

        // 실행
        EntityNotFoundException exception = new EntityNotFoundException(message);

        // 검증
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should be a RuntimeException")
    void shouldBeRuntimeException() {
        // 준비
        EntityNotFoundException exception = new EntityNotFoundException("Test message");

        // 실행 및 검증
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should preserve null message")
    void shouldPreserveNullMessage() {
        // 준비 및 실행
        EntityNotFoundException exception = new EntityNotFoundException(null);

        // 검증
        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should handle empty message")
    void shouldHandleEmptyMessage() {
        // 준비
        String message = "";

        // 실행
        EntityNotFoundException exception = new EntityNotFoundException(message);

        // 검증
        assertEquals("", exception.getMessage());
    }
}
