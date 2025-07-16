package com.loopers.infrastructure.point;

import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public PointModel getPointByUserId(String userId) {
        return pointJpaRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public PointModel save(PointModel pointModel) {
        return pointJpaRepository.save(pointModel);
    }
}
