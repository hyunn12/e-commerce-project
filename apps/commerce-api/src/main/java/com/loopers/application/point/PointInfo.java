package com.loopers.application.point;

import com.loopers.domain.point.Point;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointInfo {

    private Long userId;
    private int point;

    public static PointInfo from(Point point) {
        return new PointInfo(
                point.getUserId(),
                point.getPoint()
        );
    }
}
