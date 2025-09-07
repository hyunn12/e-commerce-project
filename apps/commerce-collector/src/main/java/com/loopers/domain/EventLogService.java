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
}
