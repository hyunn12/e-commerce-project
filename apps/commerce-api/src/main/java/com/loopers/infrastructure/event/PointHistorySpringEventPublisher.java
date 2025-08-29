package com.loopers.infrastructure.event;

import com.loopers.domain.event.PointHistoryEventPublisher;
import com.loopers.domain.event.dto.PointHistoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class PointHistorySpringEventPublisher implements PointHistoryEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(PointHistoryEvent event) {
        eventPublisher.publishEvent(event);
    }
}
