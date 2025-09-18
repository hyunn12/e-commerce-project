package com.loopers.domain.ranking;

import java.util.List;

public interface ProductWeeklyRankingRepository {

    void saveAll(List<ProductWeeklyRanking> items);
}
