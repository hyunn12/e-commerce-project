package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.ProductWeeklyRanking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductWeeklyRankingJpaRepository extends JpaRepository<ProductWeeklyRanking, Long> {

    List<ProductWeeklyRanking> findByYearWeekOrderByTotalScoreDesc(String yearWeek, Pageable pageable);

    long countByYearWeek(String yearWeek);
}
