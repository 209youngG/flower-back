package com.flower.store.dto;

import com.flower.store.domain.Store;
import com.flower.store.domain.StoreStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record StoreDto(
        Long id,
        Long ownerId,
        String name,
        String address,
        Double lat,
        Double lon,
        String phone,
        String description,
        LocalTime openTime,
        LocalTime closeTime,
        List<String> closedDays,
        StoreStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static StoreDto from(Store store) {
        return new StoreDto(
                store.getId(),
                store.getOwnerId(),
                store.getName(),
                store.getAddress(),
                store.getLat(),
                store.getLon(),
                store.getPhone(),
                store.getDescription(),
                store.getOpenTime(),
                store.getCloseTime(),
                store.getClosedDays(),
                store.getStatus(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        );
    }
}
