package com.flower.common.event;

import lombok.Getter;

@Getter
public class ReviewDeletedEvent extends DomainEvent {
    private final Long reviewId;
    private final Long productId;
    private final Integer rating;

    public ReviewDeletedEvent(Object source, Long reviewId, Long productId, Integer rating) {
        super(source);
        this.reviewId = reviewId;
        this.productId = productId;
        this.rating = rating;
    }
}
