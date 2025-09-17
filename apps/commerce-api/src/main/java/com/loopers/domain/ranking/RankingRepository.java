package com.loopers.domain.ranking;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RankingRepository {

    List<ProductWeeklyRanking> findWeeklyRankings(String yearWeek, Pageable pageable);
    long countWeeklyRankings(String yearWeek);

    List<ProductMonthlyRanking> findMonthlyRankings(String yearMonth, Pageable pageable);
    long countMonthlyRankings(String yearMonth);
}
