package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;
import jakarta.validation.constraints.NotNull;

public class PointDto {

    public record ChargeRequest(
            @NotNull
            int point
    ) {
        public PointInfo toInfo(String userId) {
            return new PointInfo(userId, point);
        }
    }

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
