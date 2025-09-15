package com.loopers.infrastructure;

import com.loopers.domain.ranking.ProductMonthlyRanking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMonthlyRankingJpaRepository extends JpaRepository<ProductMonthlyRanking, Long> {
}
