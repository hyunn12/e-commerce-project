package com.loopers.infrastructure.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Point getPointByUserId(String userId) {
        return pointJpaRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }
}
