package com.loopers.application.point;

import com.loopers.domain.point.PointModel;

public record PointInfo(String userId, int point) {

    public static PointInfo from(PointModel model) {
        return new PointInfo(
                model.getUserId(),
                model.getPoint()
        );
    }

    public PointModel toModel() {
        return new PointModel(
                userId,
                point
        );
    }
}
