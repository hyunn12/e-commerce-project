package com.loopers.infrastructure;

import com.loopers.domain.metrics.ProductMetricsCount;
import com.loopers.domain.metrics.ProductMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductMetricsRepositoryImpl implements ProductMetricsRepository {

    private final ProductMetricsJpaRepository productMetricsJpaRepository;

    @Override
    public void upsert(Long productId, ProductMetricsCount productMetricsCount) {
        productMetricsJpaRepository.upsert(
                productId,
                productMetricsCount.getLikeCount(),
                productMetricsCount.getSalesCount(),
                productMetricsCount.getViewCount()
        );
    }
}
