package com.flower.delivery.service;

import com.flower.common.event.OrderPlacedEvent;
import com.flower.common.event.PaymentCompletedEvent;
import com.flower.delivery.domain.Delivery;
import com.flower.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.flower.delivery.dto.DeliveryDto;
import com.flower.delivery.dto.UpdateDeliveryStatusRequest;
import com.flower.common.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    /**
     * 결제 완료 시 배송 준비 시작
     * (기존 OrderPlacedEvent 리스너는 PaymentCompletedEvent로 대체하여 결제 완료된 주문만 배송 처리)
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("결제 완료 이벤트 수신 - 배송 생성 시작: 주문번호={}", event.getOrderNumber());
        
        // 배송 정보가 없는 경우 (예: 픽업) 고려 필요하지만, 일단 기본 배송 생성
        Delivery delivery = Delivery.builder()
                .orderId(event.getOrderId()) // PaymentCompletedEvent field is getOrderId()
                .orderNumber(event.getOrderNumber())
                .status(Delivery.DeliveryStatus.PREPARING) // 결제 완료 시 바로 상품 준비 단계로 진입
                .build();
        
        // TODO: 배송 주소 정보 추가 필요.
        // 현재는 Order 정보를 다시 조회하거나 이벤트를 확장해야 함.
        // 여기서는 MVP로 일단 생성만 수행.
        
        deliveryRepository.save(delivery);
        log.info("배송 생성 완료: 배송ID={}, 상태=PREPARING", delivery.getId());
    }

    @Transactional(readOnly = true)
    public List<DeliveryDto> getAllDeliveries() {
        return deliveryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeliveryDto getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("배송 정보를 찾을 수 없습니다. Order ID: " + orderId));
        return toDto(delivery);
    }

    @Transactional
    public DeliveryDto updateDeliveryStatus(Long deliveryId, UpdateDeliveryStatusRequest request) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("배송 정보를 찾을 수 없습니다. ID: " + deliveryId));

        // 상태 전이 유효성 검사 (간단하게)
        validateStatusTransition(delivery.getStatus(), request.status());

        delivery.setStatus(request.status());
        
        if (request.status() == Delivery.DeliveryStatus.SHIPPING) {
            delivery.setTrackingNumber(request.trackingNumber());
            delivery.setCourierName(request.courierName());
            delivery.setStartedAt(LocalDateTime.now());
        } else if (request.status() == Delivery.DeliveryStatus.COMPLETED) {
            delivery.setCompletedAt(LocalDateTime.now());
        }

        Delivery saved = deliveryRepository.save(delivery);
        log.info("배송 상태 변경: ID={}, Status={}", saved.getId(), saved.getStatus());
        
        return toDto(saved);
    }
    
    private void validateStatusTransition(Delivery.DeliveryStatus current, Delivery.DeliveryStatus next) {
        if (current == Delivery.DeliveryStatus.COMPLETED || current == Delivery.DeliveryStatus.FAILED) {
            throw new IllegalStateException("이미 종료된 배송 상태는 변경할 수 없습니다: " + current);
        }
    }

    private DeliveryDto toDto(Delivery delivery) {
        return new DeliveryDto(
            delivery.getId(),
            delivery.getOrderId(),
            delivery.getOrderNumber(),
            delivery.getReceiverName(),
            delivery.getReceiverPhone(),
            delivery.getAddress(),
            delivery.getStatus(),
            delivery.getTrackingNumber(),
            delivery.getCourierName(),
            delivery.getStartedAt(),
            delivery.getCompletedAt()
        );
    }
}
