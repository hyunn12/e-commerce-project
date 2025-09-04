package com.loopers.domain.event;

import com.loopers.domain.event.dto.StockDecreaseEvent;
import com.loopers.domain.event.dto.StockIncreaseEvent;

public interface StockEventPublisher {

    void publish(StockIncreaseEvent event);

    void publish(StockDecreaseEvent event);
}
