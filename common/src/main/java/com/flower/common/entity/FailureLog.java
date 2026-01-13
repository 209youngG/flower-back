package com.flower.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "failure_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String domain; // e.g., "ORDER", "PAYMENT"

    @Column(name = "reference_id", nullable = false)
    private String referenceId; // e.g., OrderNumber

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String payload; // 관련 데이터 (JSON 등)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProcessingStatus status = ProcessingStatus.PENDING;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void markAsResolved() {
        this.status = ProcessingStatus.RESOLVED;
    }
    
    public void markAsFailed() {
        // 재시도 횟수 초과 등으로 인한 영구 실패 처리 시 사용 가능
        this.status = ProcessingStatus.FAILED; 
    }

    public enum ProcessingStatus {
        PENDING, RESOLVED, FAILED
    }
}
