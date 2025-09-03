package com.loopers.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Entity
@Table(name = "event_handled")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventHandled {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "handled_at", nullable = false)
    private ZonedDateTime handledAt = ZonedDateTime.now();

    private EventHandled(String eventId) {
        this.eventId = eventId;
    }

    public static EventHandled of(String eventId) {
        return new EventHandled(eventId);
    }
}
