package com.loopers.infrastructure.event.publisher;

import com.loopers.domain.event.OrderEventPublisher;
import com.loopers.domain.event.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisherImpl implements OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(OrderCreatedEvent event) {
        log.info("Order Success Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }
}
