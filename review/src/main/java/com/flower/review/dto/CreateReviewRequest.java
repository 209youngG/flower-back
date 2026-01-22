package com.flower.review.dto;

public record CreateReviewRequest(
    Long productId,
    Long memberId,
    Long orderItemId,
    Integer rating,
    String content
) {
    public CreateReviewRequest {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }
}
