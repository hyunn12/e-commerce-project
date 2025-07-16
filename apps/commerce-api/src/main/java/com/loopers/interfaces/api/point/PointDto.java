package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;

public class PointDto {

    public record ChargeRequest(
            String userId,
            int point
    ) { }

    public record PointResponse(
            String userId,
            int point
    ) {
        public static PointResponse from(PointInfo info) {
            return new PointResponse(
                    info.userId(),
                    info.point()
            );
        }
    }
}
