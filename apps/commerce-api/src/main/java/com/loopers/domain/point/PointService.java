package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public Point getPointByUserId(Long userId) {
        return pointRepository.getPointByUserId(userId);
    }

    public Point charge(Point point) {
        Point currentPoint = pointRepository.getPointByUserId(point.getUserId());
        if (currentPoint == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "포인트 정보를 찾을 수 없습니다.");
        }

        currentPoint.addPoint(point.getPoint());
        return pointRepository.save(currentPoint);
    }

    public Point save(Point point) {
        return pointRepository.save(point);
    }
}
