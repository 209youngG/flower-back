package com.flower.cart.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장바구니 상품 추가 요청")
public record AddCartItemRequest(
    @Schema(description = "회원 ID", example = "1")
    Long memberId,

    @Schema(description = "상품 ID", example = "10")
    Long productId,

    @Schema(description = "담을 수량", example = "1")
    int quantity,

    @Schema(description = "선택한 상품 옵션 ID 목록")
    List<Long> optionIds
) {}
