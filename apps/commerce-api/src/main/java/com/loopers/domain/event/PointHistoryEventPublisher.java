package com.loopers.domain.event;

import com.loopers.domain.event.dto.PointHistoryEvent;

public interface PointHistoryEventPublisher {

    void publish(PointHistoryEvent event);
}
