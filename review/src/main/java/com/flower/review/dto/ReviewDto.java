package com.flower.review.dto;

import java.time.LocalDateTime;

public record ReviewDto(
    Long id,
    Long productId,
    Long memberId,
    Integer rating,
    String content,
    LocalDateTime createdAt
) {}
