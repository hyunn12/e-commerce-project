package com.loopers.domain.point;

import com.loopers.domain.event.PointHistoryEventPublisher;
import com.loopers.domain.event.dto.PointHistoryEvent;
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
    private final PointHistoryEventPublisher historyEventPublisher;

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

        historyEventPublisher.publish(PointHistoryEvent.of(point.getUserId(), PointType.CHARGE, point.getPoint(), null));

        return currentPoint;
    }

    public Point save(Point point) {
        return pointRepository.save(point);
    }

    @Transactional
    public void use(Long userId, int amount, Long orderId) {
        Point point = getDetailByUserId(userId);
        point.use(amount);

        historyEventPublisher.publish(PointHistoryEvent.of(point.getUserId(), PointType.USE, amount, orderId));
    }

    @Transactional
    public void useWithLock(Long userId, int amount, Long orderId) {
        Point point = getDetailByUserIdWithLock(userId);
        point.use(amount);

        historyEventPublisher.publish(PointHistoryEvent.of(point.getUserId(), PointType.USE, amount, orderId));
    }

    @Transactional
    public void restoreWithLock(Long userId, int amount, Long orderId) {
        Point point = getDetailByUserIdWithLock(userId);
        point.add(amount);

        historyEventPublisher.publish(PointHistoryEvent.of(point.getUserId(), PointType.RESTORE, amount, orderId));
    }

    @Transactional
    public void saveHistory(PointHistory history) {
        pointRepository.saveHistory(history);
    }
}
