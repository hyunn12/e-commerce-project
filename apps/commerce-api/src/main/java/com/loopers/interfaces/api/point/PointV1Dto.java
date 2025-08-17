package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointCommand;
import com.loopers.application.point.PointInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import static com.loopers.support.utils.Validation.Message.MESSAGE_POINT_CHARGE;

public class PointV1Dto {

    public record ChargeRequest(
            @NotNull @Min(value = 1, message = MESSAGE_POINT_CHARGE)
            int amount
    ) {
        public PointCommand.Charge toCommand(Long userId) {
            return PointCommand.Charge.builder().userId(userId).amount(amount).build();
        }
    }

    public record PointResponse(
            Long userId,
            int amount
    ) {
        public static PointResponse from(PointInfo info) {
            return new PointResponse(
                    info.getUserId(),
                    info.getPoint()
            );
        }
    }
}
