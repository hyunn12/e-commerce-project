package com.loopers.application.point;

import com.loopers.domain.point.Point;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointCommand {

    @Getter
    @Builder
    public static class Charge {
        private String userId;
        private int amount;

        public Point toDomain() {
            return new Point(
                    userId,
                    amount
            );
        }
    }
}
