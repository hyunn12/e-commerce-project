package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.ProductLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeJpaRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndProductId(Long userId, Long productId);

    @Query("""
        SELECT new com.loopers.domain.like.ProductLikeCount(l.productId, COUNT(l))
        FROM Like l
        WHERE l.deletedAt IS NULL
        GROUP BY l.productId
    """)
    List<ProductLikeCount> countLikesGroupByProduct();
}
