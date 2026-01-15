package com.flower.payment.service;

import com.flower.common.event.OrderPlacedEvent;
import com.flower.common.event.PaymentCompletedEvent;
import com.flower.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void processPayment(Long orderId, String method) {
        log.info("Processing payment for order: {}, method: {}", orderId, method);

        // 1. 결제 승인 로직 (Mock)
        // 실제 PG사 연동 대신 성공으로 가정
        
        // 무통장 입금인 경우 즉시 결제 완료 처리하지 않음 (관리자 확인 필요)
        if ("BANK_TRANSFER".equalsIgnoreCase(method)) {
            log.info("Bank transfer requested for order: {}. Waiting for confirmation.", orderId);
            return;
        }
        
        // 2. 주문 상태 변경 (PAID)
        orderService.markAsPaid(orderId);
        
        // 3. 재고 차감 요청 (이벤트 발행)
        var order = orderService.getOrderDetail(orderId);
        
        var items = order.items().stream()
                .map(item -> new OrderPlacedEvent.OrderItemInfo(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new PaymentCompletedEvent(
                order.orderNumber(),
                order.id(),
                items
        ));
        
        log.info("Payment completed for order: {}", orderId);
    }

    @Transactional
    public void cancelPayment(Long orderId) {
        log.info("Cancelling payment for order: {}", orderId);
        
        // 1. PG사 결제 취소 요청 로직 (Mock)
        // 실제로는 PG사 API를 호출하여 취소 승인을 받아야 함
        
        // 2. 주문 결제 상태 변경 (REFUNDED)
        orderService.markAsRefunded(orderId);
        
        log.info("Payment cancelled for order: {}", orderId);
    }
}
