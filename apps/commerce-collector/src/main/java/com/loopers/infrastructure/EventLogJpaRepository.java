package com.loopers.infrastructure;

import com.loopers.domain.eventLog.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventLogJpaRepository extends JpaRepository<EventLog, Long> {

    boolean existsByEventId(String eventId);

    Optional<EventLog> findByEventId(String eventId);
}
