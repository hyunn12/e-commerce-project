package com.loopers.domain;

import java.util.Optional;

public interface EventLogRepository {

    void save(EventLog eventLog);

    boolean existsByEventId(String eventId);

    Optional<EventLog> findByEventId(String eventId);
}
