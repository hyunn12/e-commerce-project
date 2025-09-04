package com.loopers.domain.event;

import com.loopers.domain.event.dto.BrandModifyEvent;

public interface BrandEventPublisher {

    void publish(BrandModifyEvent event);
}
