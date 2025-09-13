package com.loopers.domain.metrics;

public interface ProductMetricsRepository {

    void upsert(Long productId, ProductMetricsCount productMetricsCount);
}
