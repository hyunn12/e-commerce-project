package com.loopers.domain.ranking;

import java.util.List;

public interface ProductMonthlyRankingRepository {

    void saveAll(List<ProductMonthlyRanking> items);
}
