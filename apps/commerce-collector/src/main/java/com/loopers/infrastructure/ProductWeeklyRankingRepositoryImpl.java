package com.loopers.infrastructure;

import com.loopers.domain.ranking.ProductWeeklyRanking;
import com.loopers.domain.ranking.ProductWeeklyRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductWeeklyRankingRepositoryImpl implements ProductWeeklyRankingRepository {

    private final ProductWeeklyRankingJpaRepository productWeeklyRankingJpaRepository;

    @Override
    public void saveAll(List<ProductWeeklyRanking> items) {
        productWeeklyRankingJpaRepository.saveAll(items);
    }
}
