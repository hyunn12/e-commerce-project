package com.loopers.domain.event;

public interface OrderEventPublisher {

    void publish(OrderCreatedEvent event);
}
