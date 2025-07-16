package com.loopers.domain.point;

import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository {

    PointModel getPointByUserId(String userId);

    PointModel save(PointModel pointModel);

}
