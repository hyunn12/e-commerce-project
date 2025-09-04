package com.loopers.infrastructure.event.publisher;

import com.loopers.domain.event.ProductEventPublisher;
import com.loopers.domain.event.dto.ProductViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisherImpl implements ProductEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(ProductViewEvent event) {
        log.info("Product View Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }
}
