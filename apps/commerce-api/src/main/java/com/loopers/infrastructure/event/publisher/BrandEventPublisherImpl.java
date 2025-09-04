package com.loopers.infrastructure.event.publisher;

import com.loopers.domain.event.BrandEventPublisher;
import com.loopers.domain.event.dto.BrandModifyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BrandEventPublisherImpl implements BrandEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(BrandModifyEvent event) {
        log.info("Brand Modify Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }
}
