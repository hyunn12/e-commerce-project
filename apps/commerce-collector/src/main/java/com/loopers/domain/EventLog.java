package com.loopers.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Entity
@Table(name = "event_log")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventLog extends BaseEntity {

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "version", nullable = false)
    private String version;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "published_at", nullable = false)
    private ZonedDateTime publishedAt;

    @Column(name = "received_at", nullable = false)
    private ZonedDateTime receivedAt = ZonedDateTime.now();

    private EventLog(String eventId, String eventType, String payload, ZonedDateTime publishedAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
        this.publishedAt = publishedAt;
    }

    public static EventLog of(String eventId, String eventType, String payload, ZonedDateTime publishedAt) {
        return new EventLog(eventId, eventType, payload, publishedAt);
    }
}
