package com.loopers.infrastructure;

import com.loopers.domain.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLogJpaRepository extends JpaRepository<EventLog, Long> {

    boolean existsByEventId(String eventId);
}
