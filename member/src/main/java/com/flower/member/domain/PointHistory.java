package com.flower.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 포인트 거래 내역을 추적하는 PointHistory 엔티티
 */
@Entity
@Table(name = "point_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false, length = 20)
    private PointType pointType;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "balance_after", precision = 10, scale = 2, nullable = false)
    private BigDecimal balanceAfter;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * 포인트 거래 유형 Enum
     */
    public enum PointType {
        EARN,      // 포인트 적립 (주문 완료, 이벤트 참여 등)
        USE,       // 포인트 사용 (결제 시)
        ADJUST,    // 포인트 조정 (관리자에 의한 정정)
        EXPIRE     // 포인트 소멸 (유효기간 만료)
    }
}
