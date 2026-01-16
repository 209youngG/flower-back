package com.flower.api.controller;

import com.flower.delivery.dto.DeliveryDto;
import com.flower.delivery.dto.UpdateDeliveryStatusRequest;
import com.flower.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
@Tag(name = "Delivery", description = "배송 관리 API")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "배송 정보 조회", description = "주문 번호에 해당하는 배송 정보를 조회하거나, 전체 배송 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getDeliveries(@RequestParam(required = false) Long orderId) {
        if (orderId != null) {
            return ResponseEntity.ok(deliveryService.getDeliveryByOrderId(orderId));
        } else {
            return ResponseEntity.ok(deliveryService.getAllDeliveries());
        }
    }

    @Operation(summary = "배송 상태 수정", description = "배송 상태를 변경합니다 (관리자 전용).")
    @PutMapping("/{deliveryId}")
    public ResponseEntity<DeliveryDto> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @RequestBody UpdateDeliveryStatusRequest request) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(deliveryId, request));
    }
}
