package com.flower.order.dto;

import com.flower.order.domain.Order.DeliveryMethod;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateOrderRequest {
    private Long memberId;
    private DeliveryMethod deliveryMethod;
    private LocalDateTime reservedAt;
    private String messageCard;
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryName;
    private String deliveryNote;
}
