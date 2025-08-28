package com.loopers.domain.event;

import com.loopers.domain.event.dto.ProductViewEvent;

public interface ProductEventPublisher {

    void publish(ProductViewEvent event);
}
