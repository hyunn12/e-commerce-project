package com.loopers.domain.like;

import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository {

    Like findLike(Like like);

    Like save(Like like);

}
