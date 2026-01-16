package com.flower.delivery.dto;

import com.flower.delivery.domain.Delivery.DeliveryStatus;
import java.time.LocalDateTime;

public record DeliveryDto(
    Long id,
    Long orderId,
    String orderNumber,
    String receiverName,
    String receiverPhone,
    String address,
    DeliveryStatus status,
    String trackingNumber,
    String courierName,
    LocalDateTime startedAt,
    LocalDateTime completedAt
) {}
