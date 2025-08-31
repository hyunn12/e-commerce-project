package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.like.ProductLikeCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;

    @Override
    public Like findLike(Like like) {
        return likeJpaRepository.findByUserIdAndProductId(like.getUserId(), like.getProductId()).orElse(null);
    }

    @Override
    public Like save(Like like) {
        return likeJpaRepository.save(like);
    }

    @Override
    public List<ProductLikeCount> countLikesGroupByProduct() {
        return likeJpaRepository.countLikesGroupByProduct();
    }
}
