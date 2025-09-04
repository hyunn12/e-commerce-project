package com.loopers.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductMetricsService {

    private final ProductMetricsRepository productMetricsRepository;

    @Transactional
    public void increaseLikeCount(Long productId) {
        productMetricsRepository.increaseLikeCount(productId);
    }

    @Transactional
    public void decreaseLikeCount(Long productId) {
        productMetricsRepository.decreaseLikeCount(productId);
    }

    @Transactional
    public void increaseSalesCount(Long productId, int quantity) {
        productMetricsRepository.increaseSalesCount(productId, quantity);
    }

    @Transactional
    public void decreaseSalesCount(Long productId, int quantity) {
        productMetricsRepository.decreaseSalesCount(productId, quantity);
    }

    @Transactional
    public void increaseViewCount(Long productId) {
        productMetricsRepository.increaseViewCount(productId);
    }
}
