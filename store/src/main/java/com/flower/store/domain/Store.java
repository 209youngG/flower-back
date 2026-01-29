package com.flower.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stores")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;

    @Column(nullable = false)
    private String phone;

    @Column(length = 2000)
    private String description;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @ElementCollection
    @Column(name = "closed_day")
    @Builder.Default
    private List<String> closedDays = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StoreStatus status = StoreStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void approve() {
        if (this.status != StoreStatus.PENDING) {
            throw new IllegalStateException("승인 대기 중인 매장만 승인할 수 있습니다.");
        }
        this.status = StoreStatus.APPROVED;
    }

    public void reject() {
        if (this.status != StoreStatus.PENDING) {
            throw new IllegalStateException("승인 대기 중인 매장만 거절할 수 있습니다.");
        }
        this.status = StoreStatus.REJECTED;
    }

    public void updateInfo(String name, String address, Double lat, Double lon,
                           String phone, String description, LocalTime openTime, 
                           LocalTime closeTime, List<String> closedDays) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.phone = phone;
        this.description = description;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.closedDays = closedDays != null ? closedDays : new ArrayList<>();
    }

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
