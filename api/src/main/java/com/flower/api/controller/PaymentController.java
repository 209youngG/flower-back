package com.flower.api.controller;

import com.flower.api.dto.PaymentRequest;
import com.flower.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 관리 API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 승인", description = "주문에 대한 결제를 수행합니다.")
    @PostMapping
    public ResponseEntity<Void> processPayment(@RequestBody PaymentRequest request) {
        // API DTO -> Service DTO 변환
        var serviceRequest = new com.flower.payment.dto.PaymentRequest(
            request.orderNumber(),
            request.orderId(),
            request.amount(),
            request.paymentKey(),
            request.paymentMethod(),
            "주문 결제" // orderName은 임시로 고정
        );
        
        paymentService.processPayment(serviceRequest);
        return ResponseEntity.ok().build();
    }
}
