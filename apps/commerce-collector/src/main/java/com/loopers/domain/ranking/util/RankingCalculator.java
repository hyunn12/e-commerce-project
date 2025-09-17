package com.loopers.domain.ranking.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class RankingCalculator {

    private final BigDecimal viewWeight;
    private final BigDecimal likeWeight;
    private final BigDecimal salesWeight;

    public RankingCalculator(
            @Value("${ranking.weight.view}") double viewWeight,
            @Value("${ranking.weight.like}") double likeWeight,
            @Value("${ranking.weight.sales}") double salesWeight
    ) {
        this.viewWeight = BigDecimal.valueOf(viewWeight);
        this.likeWeight = BigDecimal.valueOf(likeWeight);
        this.salesWeight = BigDecimal.valueOf(salesWeight);
    }

    public BigDecimal calculateScore(long viewCount, long likeCount, long salesCount) {
        BigDecimal viewScore = BigDecimal.valueOf(viewCount).multiply(viewWeight);
        BigDecimal likeScore = BigDecimal.valueOf(likeCount).multiply(likeWeight);
        BigDecimal salesScore = BigDecimal.valueOf(salesCount).multiply(salesWeight);

        return viewScore.add(likeScore).add(salesScore).setScale(2, RoundingMode.HALF_UP);
    }
}
