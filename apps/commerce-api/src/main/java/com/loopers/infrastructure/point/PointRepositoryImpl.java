package com.loopers.infrastructure.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointHistory;
import com.loopers.domain.point.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;
    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public Point getPointByUserId(Long userId) {
        return pointJpaRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public Point getPointByUserIdWithLock(Long userId) {
        return pointJpaRepository.findByUserIdWithLock(userId).orElse(null);
    }

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public void saveHistory(PointHistory pointHistory) {
        pointHistoryJpaRepository.save(pointHistory);
    }
}
