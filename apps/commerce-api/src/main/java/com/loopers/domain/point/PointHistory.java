package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PointType type;

    @Column(name = "order_id")
    private Long orderId;

    private PointHistory(Long userId, int amount, PointType type, Long orderId) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.orderId = orderId;
    }

    public static PointHistory of(Long userId, int amount, PointType type, Long orderId) {
        return new PointHistory(userId, amount, type, orderId);
    }
}
