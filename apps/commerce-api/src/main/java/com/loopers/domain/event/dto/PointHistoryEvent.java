package com.loopers.domain.event.dto;

import com.loopers.domain.point.PointType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointHistoryEvent {

    private Long userId;
    private int amount;
    private PointType type;
    private Long orderId;

    public static PointHistoryEvent of(Long userId, PointType type, int amount, Long orderId) {
        return new PointHistoryEvent(userId, amount, type, orderId);
    }
}
