package com.loopers.infrastructure.event.publisher;

import com.loopers.domain.event.*;
import com.loopers.domain.event.dto.PaymentCallbackFailEvent;
import com.loopers.domain.event.dto.PaymentFailEvent;
import com.loopers.domain.event.dto.PaymentRequestSuccessEvent;
import com.loopers.domain.event.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

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
