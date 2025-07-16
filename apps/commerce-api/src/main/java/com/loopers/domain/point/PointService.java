package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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
        if (currentPoint == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "포인트 정보를 찾을 수 없습니다.");
        }

        currentPoint.addPoint(pointModel.getPoint());
        return pointRepository.save(currentPoint);
    }

    public PointModel save(PointModel pointModel) {
        return pointRepository.save(pointModel);
    }
}
