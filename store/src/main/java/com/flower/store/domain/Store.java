package com.flower.store.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;

    private String name;

    private String address;

    private Double lat;

    private Double lon;

    @Enumerated(EnumType.STRING)
    private StoreStatus status;

    @Builder
    public Store(Long ownerId, String name, String address, Double lat, Double lon, StoreStatus status) {
        this.ownerId = ownerId;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
    }
}
