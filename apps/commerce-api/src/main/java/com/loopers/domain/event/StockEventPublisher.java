package com.loopers.domain.event;

import com.loopers.domain.event.dto.StockEvent;

public interface StockEventPublisher {

    void publish(StockEvent event);
}
