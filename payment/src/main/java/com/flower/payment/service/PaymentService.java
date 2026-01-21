package com.flower.payment.service;

import com.flower.common.exception.PaymentProcessingException;
import com.flower.common.event.OrderPlacedEvent;
import com.flower.common.event.PaymentCompletedEvent;
import com.flower.order.service.OrderService;
import com.flower.payment.dto.PaymentRequest;
import com.flower.payment.port.out.PaymentGatewayPort;
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
    private final PaymentGatewayPort paymentGatewayPort;

    @Transactional
    public void processPayment(PaymentRequest request) {
        log.info("Processing payment for order: {}, method: {}", request.orderNumber(), request.paymentType());

        try {
            // 1. PG사 결제 승인 요청
            boolean isApproved = paymentGatewayPort.processPayment(
                request.orderNumber(), 
                request.amount(), 
                request.paymentKey(), 
                request.paymentType()
            );

            if (!isApproved) {
                throw new PaymentProcessingException("PG사 결제 승인 거절");
            }
            
            // 2. 주문 상태 변경 (PAID)
            orderService.markAsPaid(request.orderId());
            
            // 3. 결제 완료 이벤트 발행 (배송 등 후속 처리용)
            var order = orderService.getOrderDetail(request.orderId());
            
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
                    order.isDirectOrder(),
                    order.deliveryName(),
                    order.deliveryPhone(),
                    order.deliveryAddress(),
                    order.deliveryNote()
            ));
            
            log.info("Payment completed for order: {}", request.orderId());
            
        } catch (Exception e) {
            log.error("Payment failed for order: {}, reason: {}", request.orderId(), e.getMessage());
            try {
                orderService.markAsFailed(request.orderId());
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
        
        // TODO: 실제로는 Order 엔티티에서 orderNumber를 조회해야 함
        // 지금은 데모용으로 로그만 남김
        
        // 2. 주문 결제 상태 변경 (REFUNDED)
        orderService.markAsRefunded(orderId);
        
        log.info("Payment cancelled for order: {}", orderId);
    }
    
    @Transactional
    public void cancelPaymentByOrderNumber(String orderNumber, String reason) {
        log.info("Cancelling payment via PG for orderNumber: {}", orderNumber);
        paymentGatewayPort.cancelPayment(orderNumber, reason);
    }
}
