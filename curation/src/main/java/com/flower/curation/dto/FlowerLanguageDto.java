package com.flower.curation.dto;

public record FlowerLanguageDto(
        Long id,
        String flowerName,
        String occasion,
        String meaning,
        String emotion,
        String description
) {
}
