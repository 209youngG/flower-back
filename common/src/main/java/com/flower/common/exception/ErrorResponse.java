package com.flower.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;
    private String code;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, int status, String code) {
        this.message = message;
        this.status = status;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }
}
