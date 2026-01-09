package com.flower.delivery.service;

import com.flower.common.event.OrderPlacedEvent;
import com.flower.delivery.domain.Delivery;
import com.flower.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("배송 생성 시작 - 주문 ID: {}", event.getOrderId());
        
        // 실제 시나리오에서는 주문 정보를 조회하여 배송 타입을 결정함
        // 현재는 기본 배송으로 가정
        
        Delivery delivery = Delivery.builder()
                .orderId(Long.parseLong(event.getOrderId())) // 이벤트의 orderId는 String이지만 배송 엔티티는 Long을 가정
                .status(Delivery.DeliveryStatus.PENDING)
                .build();
        
        deliveryRepository.save(delivery);
        log.info("배송 생성 완료: 배송ID={}", delivery.getId());
    }
}
