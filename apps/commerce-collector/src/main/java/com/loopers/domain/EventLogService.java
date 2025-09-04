package com.loopers.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventLogService {

    private final EventLogRepository eventLogRepository;

    public void save(EventLog eventLog) {
        eventLogRepository.save(eventLog);
    }

    public boolean existsByEventId(String eventId) {
        return eventLogRepository.existsByEventId(eventId);
    }

    public EventLog getDetailByEventId(String eventId) {
        return eventLogRepository.findByEventId(eventId).orElse(null);
    }
}
