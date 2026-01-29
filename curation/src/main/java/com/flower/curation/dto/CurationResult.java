package com.flower.curation.dto;

import com.flower.product.dto.ProductDto;

import java.util.List;

public record CurationResult(
        List<ProductDto> bestSeller,
        List<ProductDto> storytelling,
        List<ProductDto> smartChoice,
        List<FlowerLanguageDto> flowerLanguages,
        String recommendationReason
) {
}
