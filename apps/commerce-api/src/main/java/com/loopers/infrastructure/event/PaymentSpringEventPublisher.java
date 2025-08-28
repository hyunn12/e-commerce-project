package com.loopers.infrastructure.event;

import com.loopers.domain.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class PaymentSpringEventPublisher implements PaymentEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(PaymentRequestSuccessEvent event) {
        log.info("Payment Request Success Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(PaymentSuccessEvent event) {
        log.info("Payment Success Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(PaymentFailEvent event) {
        log.info("Payment Fail Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(PaymentCallbackFailEvent event) {
        log.info("Payment Callback Fail Event Published: {}", event);
        eventPublisher.publishEvent(event);
    }
}
