package com.flower.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장바구니 상품 수량 수정 요청")
public record UpdateCartItemRequest(
    @Schema(description = "변경할 수량", example = "3")
    int quantity
) {}
