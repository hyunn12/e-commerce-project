package com.loopers.batch.processor;

import com.loopers.batch.dto.ProductRankingAggregateResult;
import com.loopers.domain.ranking.ProductMonthlyRanking;
import com.loopers.domain.ranking.util.RankingCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@StepScope
@Component
@RequiredArgsConstructor
public class MonthlyRankingProcessor implements ItemProcessor<ProductRankingAggregateResult, ProductMonthlyRanking> {

    private final RankingCalculator rankingCalculator;

    @Value("#{jobParameters['yearMonth']}")
    private String yearMonth;

    @Override
    public ProductMonthlyRanking process(ProductRankingAggregateResult item) {

        BigDecimal totalScore = rankingCalculator.calculateScore(
                item.getTotalViewCount(),
                item.getTotalLikeCount(),
                item.getTotalSalesCount()
        );

        return ProductMonthlyRanking.of(
                item.getProductId(),
                yearMonth,
                totalScore,
                item.getTotalSalesCount(),
                item.getTotalLikeCount(),
                item.getTotalViewCount()
        );
    }
}
