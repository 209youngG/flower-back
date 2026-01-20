package com.flower.api.controller;

import com.flower.api.dto.PaymentRequest;
import com.flower.order.service.OrderService;
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
    private final OrderService orderService;

    @Operation(summary = "결제 승인", description = "주문에 대한 결제를 수행합니다.")
    @PostMapping
    public ResponseEntity<Void> processPayment(@RequestBody PaymentRequest request) {
        paymentService.processPayment(request.orderId(), request.paymentMethod());
        return ResponseEntity.ok().build();
    }
}
