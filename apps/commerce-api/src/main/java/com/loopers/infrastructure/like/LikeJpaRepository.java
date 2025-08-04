package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeJpaRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndProductId(Long userId, Long productId);
}
