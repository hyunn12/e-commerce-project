package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.loopers.support.utils.Validation.Message.MESSAGE_POINT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public Point getDetailByUserId(Long userId) {
        Point point = pointRepository.getPointByUserId(userId);
        if (point == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_POINT_NOT_FOUND);
        }
        return point;
    }

    public Point getDetailByUserIdWithLock(Long userId) {
        Point point = pointRepository.getPointByUserIdWithLock(userId);
        if (point == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_POINT_NOT_FOUND);
        }
        return point;
    }

    @Transactional
    public Point charge(Point point) {
        Point currentPoint = getDetailByUserId(point.getUserId());
        currentPoint.add(point.getPoint());

        PointHistory history = PointHistory.charge(point.getUserId(), point.getPoint());
        pointRepository.saveHistory(history);

        return currentPoint;
    }

    public Point save(Point point) {
        return pointRepository.save(point);
    }

    @Transactional
    public void use(Long userId, int amount, Long orderId) {
        Point point = getDetailByUserId(userId);
        point.use(amount);

        PointHistory history = PointHistory.use(point.getUserId(), amount, orderId);
        saveHistory(history);
    }

    @Transactional
    public void useWithLock(Long userId, int amount, Long orderId) {
        Point point = getDetailByUserIdWithLock(userId);
        point.use(amount);

        PointHistory history = PointHistory.use(point.getUserId(), amount, orderId);
        saveHistory(history);
    }

    public void saveHistory(PointHistory history) {
        pointRepository.saveHistory(history);
    }
}
