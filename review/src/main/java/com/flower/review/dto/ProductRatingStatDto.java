package com.flower.review.dto;

public record ProductRatingStatDto(
    Long productId,
    Long reviewCount,
    Long totalRating
) {}
