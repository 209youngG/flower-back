package com.flower.common.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewCreatedEvent extends DomainEvent {
    private final Long reviewId;
    private final Long productId;
    private final Integer rating;
    private final LocalDateTime createdAt;

    public ReviewCreatedEvent(Object source, Long reviewId, Long productId, Integer rating) {
        super(source);
        this.reviewId = reviewId;
        this.productId = productId;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }
}
