package com.flower.payment.service;

import com.flower.common.exception.PaymentProcessingException;
import com.flower.order.dto.OrderDetailDto;
import com.flower.order.dto.OrderItemDto;
import com.flower.order.domain.OrderStatus;
import com.flower.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("결제 처리 성공 시 주문 상태를 변경하고 이벤트를 발행한다")
    void processPayment_Success() {
        // given
        Long orderId = 1L;
        String method = "CARD";
        
        OrderItemDto itemDto = OrderItemDto.builder()
                .productId(10L)
                .productName("장미")
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(10000))
                .build();

        OrderDetailDto orderDetail = new OrderDetailDto(
                orderId, "ORD-123", BigDecimal.valueOf(50000), 
                OrderStatus.PENDING.name(), "주문 대기", LocalDateTime.now(),
                "홍길동", "010-1234-5678", "서울시", "문앞", 
                100L, false, Collections.singletonList(itemDto)
        );

        given(orderService.getOrderDetail(orderId)).willReturn(orderDetail);

        // when
        paymentService.processPayment(orderId, method);

        // then
        verify(orderService).markAsPaid(orderId);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    @DisplayName("결제 모듈 내부 오류 발생 시 주문을 FAILED 상태로 변경한다")
    void processPayment_Fail_InternalError() {
        // given
        Long orderId = 1L;
        String method = "FAIL_TEST"; // 강제 예외 발생 트리거

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(orderId, method))
                .isInstanceOf(PaymentProcessingException.class);
        
        verify(orderService).markAsFailed(orderId);
    }
}
