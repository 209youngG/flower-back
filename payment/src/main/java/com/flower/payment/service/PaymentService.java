package com.flower.payment.service;

import com.flower.common.exception.PaymentProcessingException;
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

        try {
            if ("BANK_TRANSFER".equalsIgnoreCase(method)) {
                log.info("Bank transfer requested for order: {}. Waiting for confirmation.", orderId);
                return;
            }
            
            // 2. 주문 상태 변경 (PAID)
            orderService.markAsPaid(orderId);
            
            // 3. 결제 완료 이벤트 발행 (배송 등 후속 처리용)
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
                    order.memberId(),
                    items,
                    order.isDirectOrder()
            ));
            
            log.info("Payment completed for order: {}", orderId);
            
        } catch (Exception e) {
            log.error("Payment failed for order: {}, reason: {}", orderId, e.getMessage());
            try {
                orderService.markAsFailed(orderId);
            } catch (Exception ex) {
                log.error("Failed to mark order as failed during rollback: {}", ex.getMessage());
            }
            if (e instanceof PaymentProcessingException) {
                throw (PaymentProcessingException) e;
            }
            throw new PaymentProcessingException("Payment processing failed", e);
        }
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
