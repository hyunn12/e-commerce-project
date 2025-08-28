package com.loopers.domain.event;

public interface PaymentEventPublisher {

    void publish(PaymentRequestSuccessEvent event);

    void publish(PaymentSuccessEvent event);

    void publish(PaymentFailEvent event);

    void publish(PaymentCallbackFailEvent event);
}
