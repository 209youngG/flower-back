package com.flower.delivery.dto;

import com.flower.delivery.domain.Delivery.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배송 상태 수정 요청")
public record UpdateDeliveryStatusRequest(
    @Schema(description = "변경할 배송 상태 (PREPARING, SHIPPING, COMPLETED)")
    DeliveryStatus status,
    
    @Schema(description = "운송장 번호 (SHIPPING 상태 변경 시 필수)")
    String trackingNumber,
    
    @Schema(description = "택배사 (SHIPPING 상태 변경 시 필수)")
    String courierName
) {}
