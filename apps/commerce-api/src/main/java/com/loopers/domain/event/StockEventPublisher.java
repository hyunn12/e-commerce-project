package com.loopers.domain.event;

import com.loopers.domain.event.dto.StockDecreaseEvent;

public interface StockEventPublisher {

    void publish(StockDecreaseEvent event);
}
