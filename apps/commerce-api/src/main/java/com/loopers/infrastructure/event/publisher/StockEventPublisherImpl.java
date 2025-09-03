package com.loopers.infrastructure.event.publisher;

import com.loopers.domain.event.StockEventPublisher;
import com.loopers.domain.event.dto.StockDecreaseEvent;
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
    public void publish(StockDecreaseEvent event) {
        log.info("Stock Decrease Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }
}
