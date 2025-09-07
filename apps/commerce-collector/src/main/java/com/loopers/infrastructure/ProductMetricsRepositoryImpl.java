package com.loopers.infrastructure;

import com.loopers.domain.ProductMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductMetricsRepositoryImpl implements ProductMetricsRepository {

    private final ProductMetricsJpaRepository productMetricsJpaRepository;

    @Override
    public void increaseLikeCount(Long productId) {
        productMetricsJpaRepository.increaseLikeCount(productId);
    }

    @Override
    public void decreaseLikeCount(Long productId) {
        productMetricsJpaRepository.decreaseLikeCount(productId);
    }

    @Override
    public void increaseSalesCount(Long productId, int quantity) {
        productMetricsJpaRepository.increaseSalesCount(productId, quantity);
    }

    @Override
    public void decreaseSalesCount(Long productId, int quantity) {
        productMetricsJpaRepository.decreaseSalesCount(productId, quantity);
    }

    @Override
    public void increaseViewCount(Long productId) {
        productMetricsJpaRepository.increaseViewCount(productId);
    }
}
