package com.loopers.infrastructure;

import com.loopers.domain.ranking.ProductWeeklyRanking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductWeeklyRankingJpaRepository extends JpaRepository<ProductWeeklyRanking, Long> {
}
