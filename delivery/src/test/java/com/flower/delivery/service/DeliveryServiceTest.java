package com.flower.delivery.service;

import com.flower.common.event.OrderPlacedEvent;
import com.flower.common.event.PaymentCompletedEvent;
import com.flower.delivery.domain.Delivery;
import com.flower.delivery.repository.DeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @InjectMocks
    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("결제 완료 이벤트를 수신하면 배송 정보를 생성한다")
    void handlePaymentCompleted_shouldCreateDelivery() {
        // Given
        PaymentCompletedEvent event = new PaymentCompletedEvent(
            "ORD-123",
            1L,
            1L,
            Collections.emptyList(),
            false,
            "홍길동",
            "010-1234-5678",
            "서울시 강남구",
            "문 앞에 놔주세요"
        );

        // When
        deliveryService.handlePaymentCompleted(event);

        // Then
        ArgumentCaptor<Delivery> deliveryCaptor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository).save(deliveryCaptor.capture());

        Delivery savedDelivery = deliveryCaptor.getValue();
        assertThat(savedDelivery.getOrderNumber()).isEqualTo("ORD-123");
        assertThat(savedDelivery.getReceiverName()).isEqualTo("홍길동");
        assertThat(savedDelivery.getReceiverPhone()).isEqualTo("010-1234-5678");
        assertThat(savedDelivery.getAddress()).isEqualTo("서울시 강남구");
        assertThat(savedDelivery.getStatus()).isEqualTo(Delivery.DeliveryStatus.PREPARING);
    }
}
