package com.flower.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Base class for all domain events
 */
@Getter
public abstract class DomainEvent extends ApplicationEvent {

    private final String eventType;
    private final LocalDateTime occurredOn;

    public DomainEvent(Object source) {
        super(source);
        this.eventType = this.getClass().getSimpleName();
        this.occurredOn = LocalDateTime.now();
    }
}
