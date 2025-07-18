package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import static com.loopers.support.utils.Validation.Message.MESSAGE_POINT_CHARGE;

public class PointDto {

    public record ChargeRequest(
            @NotNull @Min(value = 1, message = MESSAGE_POINT_CHARGE)
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
