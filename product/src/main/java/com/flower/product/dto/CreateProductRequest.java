package com.flower.product.dto;

import com.flower.product.domain.Product.DeliveryType;
import com.flower.product.domain.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "상품 등록 요청")
public record CreateProductRequest(
    @Schema(description = "상품명", example = "기념일 장미 꽃다발")
    String name,

    @Schema(description = "상품 코드", example = "FL-001")
    String productCode,

    @Schema(description = "상품 설명", example = "특별한 날을 위한 붉은 장미")
    String description,

    @Schema(description = "판매 가격", example = "50000")
    BigDecimal price,

    @Schema(description = "재고 수량", example = "100")
    Integer stockQuantity,

    @Schema(description = "카테고리", example = "FLOWER_BOUQUET")
    ProductCategory category,

    @Schema(description = "배송 타입 (QUICK, PARCEL, MIXED)", example = "QUICK")
    DeliveryType deliveryType,

    @Schema(description = "대표 이미지 URL")
    String thumbnailUrl
) {}
