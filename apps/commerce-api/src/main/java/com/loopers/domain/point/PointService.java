package com.loopers.domain.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public PointModel getPointByUserId(String userId) {
        return pointRepository.getPointByUserId(userId);
    }

    public PointModel charge(PointModel pointModel) {
        PointModel currentPoint = pointRepository.getPointByUserId(pointModel.getUserId());
        currentPoint.addPoint(pointModel.getPoint());
        return pointRepository.save(currentPoint);
    }

    public PointModel save(PointModel pointModel) {
        return pointRepository.save(pointModel);
    }

}
