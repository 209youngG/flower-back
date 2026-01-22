package com.flower.common.event;

import lombok.Getter;

@Getter
public class ReviewUpdatedEvent extends DomainEvent {
    private final Long reviewId;
    private final Long productId;
    private final Integer oldRating;
    private final Integer newRating;

    public ReviewUpdatedEvent(Object source, Long reviewId, Long productId, Integer oldRating, Integer newRating) {
        super(source);
        this.reviewId = reviewId;
        this.productId = productId;
        this.oldRating = oldRating;
        this.newRating = newRating;
    }
}
