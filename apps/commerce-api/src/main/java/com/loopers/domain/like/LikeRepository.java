package com.loopers.domain.like;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository {

    Like findLike(Like like);

    Like save(Like like);

    List<ProductLikeCount> countLikesGroupByProduct();
}
