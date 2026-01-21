package com.flower.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 회원 도메인 엔티티
 * 꽃 쇼핑몰 시스템의 등록된 사용자를 나타냄
 */
@Entity
@Table(name = "members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String provider;

    @Column(length = 100)
    private String providerId;

    @Column(name = "point_balance", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal pointBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberGrade grade = MemberGrade.BRONZE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MemberRole role = MemberRole.USER;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PointHistory> pointHistory = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 포인트 적립
     */
    public void addPoints(BigDecimal points) {
        if (points.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("포인트는 양수여야 합니다");
        }
        this.pointBalance = this.pointBalance.add(points);
    }

    /**
     * 포인트 사용
     */
    public void usePoints(BigDecimal points) {
        if (points.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("포인트는 양수여야 합니다");
        }
        if (this.pointBalance.compareTo(points) < 0) {
            throw new IllegalStateException("포인트가 부족합니다. 보유: " + this.pointBalance + ", 요청: " + points);
        }
        this.pointBalance = this.pointBalance.subtract(points);
    }

    /**
     * 포인트 충분 여부 확인
     */
    public boolean hasSufficientPoints(BigDecimal points) {
        return this.pointBalance.compareTo(points) >= 0;
    }

    /**
     * 마지막 로그인 시간 갱신
     */
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 주소 추가
     */
    public void addAddress(Address address) {
        address.setMember(this);
        this.addresses.add(address);
    }

    /**
     * 주소 삭제
     */
    public void removeAddress(Address address) {
        this.addresses.remove(address);
        address.setMember(null);
    }
}
