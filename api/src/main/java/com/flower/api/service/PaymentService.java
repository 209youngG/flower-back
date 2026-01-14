package com.flower.api.service;

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
}
