package com.loopers.domain.event;

import com.loopers.domain.event.dto.CouponUseEvent;

public interface CouponEventPublisher {

    void publish(CouponUseEvent event);
}
