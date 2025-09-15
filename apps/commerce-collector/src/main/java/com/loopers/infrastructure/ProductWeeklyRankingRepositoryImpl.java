package com.loopers.infrastructure;

import com.loopers.domain.ranking.ProductWeeklyRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductWeeklyRankingRepositoryImpl implements ProductWeeklyRankingRepository {

    private final ProductWeeklyRankingJpaRepository productWeeklyRankingJpaRepository;
}
