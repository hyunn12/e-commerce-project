package com.loopers.infrastructure;

import com.loopers.domain.ranking.ProductMonthlyRanking;
import com.loopers.domain.ranking.ProductMonthlyRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductMonthlyRankingRepositoryImpl implements ProductMonthlyRankingRepository {

    private final ProductMonthlyRankingJpaRepository productMonthlyRankingJpaRepository;

    @Override
    public void saveAll(List<ProductMonthlyRanking> items) {
        productMonthlyRankingJpaRepository.saveAll(items);
    }
}
