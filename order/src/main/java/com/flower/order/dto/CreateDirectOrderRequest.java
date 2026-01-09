package com.flower.order.dto;

import com.flower.order.domain.Order.DeliveryMethod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "다이렉트 상품 주문 요청 DTO (장바구니 거치지 않음)")
public record CreateDirectOrderRequest(
    @Schema(description = "주문자(회원) ID", example = "1")
    Long memberId,

    @Schema(description = "상품 ID", example = "10")
    Long productId,

    @Schema(description = "주문 수량", example = "2")
    int quantity,

    @Schema(description = "선택한 옵션 목록 (옵션 ID)", nullable = true)
    List<Long> optionIds,

    @Schema(description = "배송 방법", example = "SHIPPING")
    DeliveryMethod deliveryMethod,

    @Schema(description = "예약/배송 희망 일시")
    LocalDateTime reservedAt,

    @Schema(description = "메시지 카드 내용")
    String messageCard,

    @Schema(description = "배송지 주소")
    String deliveryAddress,

    @Schema(description = "수령인 전화번호")
    String deliveryPhone,

    @Schema(description = "수령인 이름")
    String deliveryName,

    @Schema(description = "배송 요청 사항")
    String deliveryNote
) {}
