package com.loopers.infrastructure;

import com.loopers.domain.ranking.ProductMonthlyRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductMonthlyRankingRepositoryImpl implements ProductMonthlyRankingRepository {

    private final ProductMonthlyRankingJpaRepository productMonthlyRankingJpaRepository;
}
