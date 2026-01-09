package com.flower.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "상품 옵션 등록 요청")
public record CreateProductOptionRequest(
    @Schema(description = "옵션명", example = "사이즈")
    String name,

    @Schema(description = "옵션값", example = "L")
    String value,

    @Schema(description = "추가 금액", example = "5000")
    BigDecimal priceAdjustment,

    @Schema(description = "옵션별 재고 수량 (null일 경우 상품 재고 따름)", example = "50")
    Integer stockQuantity,

    @Schema(description = "표시 순서", example = "1")
    Integer displayOrder
) {}
