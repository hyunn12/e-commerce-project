package com.loopers.domain.point;

import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository {

    Point getPointByUserId(String userId);

    Point save(Point point);

}
