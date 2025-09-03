package com.loopers.infrastructure.event.publisher;

import com.loopers.domain.event.StockEventPublisher;
import com.loopers.domain.event.dto.StockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventPublisherImpl implements StockEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(StockEvent event) {
        log.info("Stock Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }
}
