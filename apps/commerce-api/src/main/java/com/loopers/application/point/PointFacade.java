package com.loopers.application.point;

import com.loopers.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointFacade {

    private final PointService pointService;

    public PointInfo getPointByUserId(Long userId) {
        return PointInfo.from(pointService.getPointByUserId(userId));
    }

    public PointInfo charge(PointCommand.Charge command) {
        return PointInfo.from(pointService.charge(command.toDomain()));
    }

}
