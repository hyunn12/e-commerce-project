package com.loopers.infrastructure;

import com.loopers.domain.handled.EventHandled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventHandledJpaRepository extends JpaRepository<EventHandled, Long> {
}
