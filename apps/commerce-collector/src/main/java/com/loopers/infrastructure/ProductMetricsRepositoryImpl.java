package com.loopers.infrastructure;

import com.loopers.domain.ProductMetricsCount;
import com.loopers.domain.ProductMetricsRepository;
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
