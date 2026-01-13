package com.flower.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 재고 입고 요청")
public record RestockProductRequest(
    @Schema(description = "입고 수량", example = "50")
    int quantity
) {}
