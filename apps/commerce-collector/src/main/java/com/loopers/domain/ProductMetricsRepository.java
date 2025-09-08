package com.loopers.domain;

public interface ProductMetricsRepository {

    void upsert(Long productId, ProductMetricsCount productMetricsCount);
}
