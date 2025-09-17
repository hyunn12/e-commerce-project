package com.loopers.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRankingAggregateResult {

    private Long productId;
    private Long totalSalesCount;
    private Long totalLikeCount;
    private Long totalViewCount;
}
