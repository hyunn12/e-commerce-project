package com.loopers.infrastructure.event;

import com.loopers.domain.event.CouponEventPublisher;
import com.loopers.domain.event.dto.CouponUseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponSpringEventPublisher implements CouponEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(CouponUseEvent event) {
        log.info("Coupon Use Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }
}
