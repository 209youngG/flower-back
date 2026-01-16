package com.flower.product.dto;

import com.flower.product.domain.Product.DeliveryType;
import com.flower.product.domain.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import java.util.List;

@Schema(description = "상품 수정 요청")
public record UpdateProductRequest(
    @Schema(description = "상품명")
    String name,

    @Schema(description = "상품 설명")
    String description,

    @Schema(description = "판매 가격")
    BigDecimal price,

    @Schema(description = "할인 가격")
    BigDecimal discountPrice,

    @Schema(description = "재고 수량")
    Integer stockQuantity,

    @Schema(description = "카테고리")
    ProductCategory category,

    @Schema(description = "배송 타입")
    DeliveryType deliveryType,

    @Schema(description = "판매 활성화 여부")
    Boolean isActive,

    @Schema(description = "당일 배송 가능 여부")
    Boolean isAvailableToday,

    @Schema(description = "대표 이미지 URL")
    String thumbnailUrl,

    @Schema(description = "상품 옵션 목록")
    List<CreateProductOptionRequest> options
) {}
