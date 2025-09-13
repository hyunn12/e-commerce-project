package com.loopers.infrastructure;

import com.loopers.domain.eventLog.EventLog;
import com.loopers.domain.eventLog.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventLogRepositoryImpl implements EventLogRepository {

    private final EventLogJpaRepository eventLogJpaRepository;

    @Override
    public void save(EventLog eventLog) {
        eventLogJpaRepository.save(eventLog);
    }
}
