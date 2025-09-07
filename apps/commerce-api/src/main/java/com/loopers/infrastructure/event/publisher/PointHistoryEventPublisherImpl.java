package com.loopers.infrastructure.event.publisher;

import com.loopers.domain.event.PointHistoryEventPublisher;
import com.loopers.domain.event.dto.PointHistoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointHistoryEventPublisherImpl implements PointHistoryEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(PointHistoryEvent event) {
        eventPublisher.publishEvent(event);
    }
}
