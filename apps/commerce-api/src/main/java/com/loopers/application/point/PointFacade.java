package com.loopers.application.point;

import com.loopers.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointFacade {

    private final PointService pointService;

    public PointInfo getPointByUserId(String userId) {
        return PointInfo.from(pointService.getPointByUserId(userId));
    }

    public PointInfo charge(PointInfo info) {
        return PointInfo.from(pointService.charge(info.toModel()));
    }

}
