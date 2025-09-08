package com.loopers.domain.handled;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Entity
@Table(name = "event_handled")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventHandled {

    @EmbeddedId
    private EventHandledId id;

    @Column(name = "handled_at", nullable = false, updatable = false)
    private ZonedDateTime handledAt;

    public EventHandled(EventHandledId id) {
        this.id = id;
    }

    public static EventHandled of(String eventId, String consumerGroup) {
        return new EventHandled(new EventHandledId(eventId, consumerGroup));
    }

    @PrePersist
    public void prePersist() {
        this.handledAt = ZonedDateTime.now();
    }
}
