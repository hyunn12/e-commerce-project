package com.loopers.infrastructure.event;

import com.loopers.domain.event.LikeEventPublisher;
import com.loopers.domain.event.dto.LikeDeleteEvent;
import com.loopers.domain.event.dto.LikeAddEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeSpringEventPublisher implements LikeEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(LikeAddEvent event) {
        log.info("Like Add Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(LikeDeleteEvent event) {
        log.info("Like Delete Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }
}
