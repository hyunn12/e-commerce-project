package com.loopers.domain.point;

import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository {

    Point getPointByUserId(Long userId);

    Point save(Point point);

    void saveHistory(PointHistory pointHistory);

}
