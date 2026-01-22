package com.flower.review.dto;

public record UpdateReviewRequest(
    Long memberId,
    Integer rating,
    String content
) {
    public UpdateReviewRequest {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }
}
