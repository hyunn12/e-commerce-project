package com.loopers.batch.processor;

import com.loopers.batch.dto.ProductRankingAggregateResult;
import com.loopers.domain.ranking.ProductWeeklyRanking;
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
public class WeeklyRankingProcessor implements ItemProcessor<ProductRankingAggregateResult, ProductWeeklyRanking> {

    private final RankingCalculator rankingCalculator;

    @Value("#{jobParameters['yearWeek']}")
    private String yearWeek;

    @Override
    public ProductWeeklyRanking process(ProductRankingAggregateResult item) {

        BigDecimal totalScore = rankingCalculator.calculateScore(
                item.getTotalViewCount(),
                item.getTotalLikeCount(),
                item.getTotalSalesCount()
        );

        return ProductWeeklyRanking.of(
                item.getProductId(),
                yearWeek,
                totalScore,
                item.getTotalSalesCount(),
                item.getTotalLikeCount(),
                item.getTotalViewCount()
        );
    }
}
