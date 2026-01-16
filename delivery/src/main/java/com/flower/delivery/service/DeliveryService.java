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

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("배송 생성 시작 - 주문번호: {}", event.getOrderNumber());
        
        Delivery delivery = Delivery.builder()
                .orderId(event.getInternalOrderId())
                .orderNumber(event.getOrderNumber())
                .status(Delivery.DeliveryStatus.PENDING)
                .build();
        
        if (event.getDeliveryInfo() != null) {
            delivery.setReceiverName(event.getDeliveryInfo().getReceiverName());
            delivery.setReceiverPhone(event.getDeliveryInfo().getPhone());
            delivery.setAddress(event.getDeliveryInfo().getAddress());
            delivery.setNote(event.getDeliveryInfo().getNote());
        }
        
        deliveryRepository.save(delivery);
        log.info("배송 생성 완료: 배송ID={}", delivery.getId());
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

        delivery.setStatus(request.status());
        
        if (request.status() == Delivery.DeliveryStatus.SHIPPING) {
            delivery.setTrackingNumber(request.trackingNumber());
            delivery.setCourierName(request.courierName());
            delivery.setStartedAt(LocalDateTime.now());
        } else if (request.status() == Delivery.DeliveryStatus.COMPLETED) {
            delivery.setCompletedAt(LocalDateTime.now());
        }

        Delivery saved = deliveryRepository.save(delivery);
        return toDto(saved);
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
