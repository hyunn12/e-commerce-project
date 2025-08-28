package com.loopers.domain.event;

import com.loopers.domain.event.dto.LikeDeleteEvent;
import com.loopers.domain.event.dto.LikeAddEvent;

public interface LikeEventPublisher {

    void publish(LikeAddEvent event);

    void publish(LikeDeleteEvent event);
}
