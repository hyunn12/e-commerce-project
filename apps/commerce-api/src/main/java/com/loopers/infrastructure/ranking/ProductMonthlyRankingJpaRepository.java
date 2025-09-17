package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.ProductMonthlyRanking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMonthlyRankingJpaRepository extends JpaRepository<ProductMonthlyRanking, Long> {

    List<ProductMonthlyRanking> findByYearMonthOrderByTotalScoreDesc(String yearMonth, Pageable pageable);

    long countByYearMonth(String yearMonth);
}
