package com.flower.order.dto;

import com.flower.order.domain.Order.DeliveryMethod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "주문 생성 요청 DTO")
public record CreateOrderRequest(
    
    @Schema(description = "주문자(회원) ID", example = "1")
    Long memberId,

    @Schema(description = "배송 방법 (PICKUP: 픽업, SHIPPING: 배송, QUICK: 퀵배송)", example = "SHIPPING")
    DeliveryMethod deliveryMethod,

    @Schema(description = "예약/배송 희망 일시", example = "2023-12-25T10:00:00")
    LocalDateTime reservedAt,

    @Schema(description = "메시지 카드 내용", example = "생일 축하해!")
    String messageCard,

    @Schema(description = "배송지 주소 (배송/퀵 선택 시 필수)", example = "서울시 강남구 테헤란로 123")
    String deliveryAddress,

    @Schema(description = "수령인 전화번호", example = "010-1234-5678")
    String deliveryPhone,

    @Schema(description = "수령인 이름", example = "홍길동")
    String deliveryName,

    @Schema(description = "배송 요청 사항", example = "문 앞에 놓아주세요")
    String deliveryNote,
    
    @Schema(description = "바로 주문 여부 (기본값: false)")
    Boolean isDirectOrder
) {}
