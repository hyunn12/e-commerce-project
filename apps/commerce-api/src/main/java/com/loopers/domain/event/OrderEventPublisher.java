package com.loopers.domain.event;

import com.loopers.domain.event.dto.OrderCreatedEvent;

public interface OrderEventPublisher {

    void publish(OrderCreatedEvent event);
}
