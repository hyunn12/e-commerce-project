package com.loopers.infrastructure.event;

import com.loopers.domain.event.OrderEventPublisher;
import com.loopers.domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class OrderSpringEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(OrderCreatedEvent event) {
        log.info("Order Success Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }
}
