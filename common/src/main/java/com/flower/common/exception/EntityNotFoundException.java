package com.flower.common.exception;

/**
 * 엔티티를 찾을 수 없을 때 발생하는 사용자 정의 예외
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
