package com.flower.store.dto;

import java.time.LocalTime;
import java.util.List;

public record UpdateStoreRequest(
        String name,
        String address,
        Double lat,
        Double lon,
        String phone,
        String description,
        LocalTime openTime,
        LocalTime closeTime,
        List<String> closedDays
) {
}
