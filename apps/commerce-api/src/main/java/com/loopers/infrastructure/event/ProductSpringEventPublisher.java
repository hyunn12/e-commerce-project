package com.loopers.infrastructure.event;

import com.loopers.domain.event.ProductEventPublisher;
import com.loopers.domain.event.dto.ProductViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class ProductSpringEventPublisher implements ProductEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(ProductViewEvent event) {
        eventPublisher.publishEvent(event);
    }
}
