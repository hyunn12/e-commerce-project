package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.ProductMonthlyRanking;
import com.loopers.domain.ranking.ProductWeeklyRanking;
import com.loopers.domain.ranking.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

    private final ProductWeeklyRankingJpaRepository productWeeklyRankingJpaRepository;
    private final ProductMonthlyRankingJpaRepository productMonthlyRankingJpaRepository;

    @Override
    public List<ProductWeeklyRanking> findWeeklyRankings(String yearWeek, Pageable pageable) {
        return productWeeklyRankingJpaRepository.findByYearWeekOrderByTotalScoreDesc(yearWeek, pageable);
    }

    @Override
    public long countWeeklyRankings(String yearWeek) {
        return productWeeklyRankingJpaRepository.countByYearWeek(yearWeek);
    }

    @Override
    public List<ProductMonthlyRanking> findMonthlyRankings(String yearMonth, Pageable pageable) {
        return productMonthlyRankingJpaRepository.findByYearMonthOrderByTotalScoreDesc(yearMonth, pageable);
    }

    @Override
    public long countMonthlyRankings(String yearMonth) {
        return productMonthlyRankingJpaRepository.countByYearMonth(yearMonth);
    }
}
