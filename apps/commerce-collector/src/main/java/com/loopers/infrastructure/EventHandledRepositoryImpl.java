package com.loopers.infrastructure;

import com.loopers.domain.handled.EventHandled;
import com.loopers.domain.handled.EventHandledRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventHandledRepositoryImpl implements EventHandledRepository {

    private final EventHandledJpaRepository eventHandledJpaRepository;

    @Override
    public void save(EventHandled eventHandled) {
        eventHandledJpaRepository.save(eventHandled);
    }
}
