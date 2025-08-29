package com.loopers.domain.event;

import com.loopers.domain.event.dto.PaymentCallbackFailEvent;
import com.loopers.domain.event.dto.PaymentFailEvent;
import com.loopers.domain.event.dto.PaymentRequestSuccessEvent;
import com.loopers.domain.event.dto.PaymentSuccessEvent;

public interface PaymentEventPublisher {

    void publish(PaymentRequestSuccessEvent event);

    void publish(PaymentSuccessEvent event);

    void publish(PaymentFailEvent event);

    void publish(PaymentCallbackFailEvent event);
}
