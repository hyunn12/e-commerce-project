package com.loopers.domain;

public interface ProductMetricsRepository {

    void increaseLikeCount(Long productId);

    void decreaseLikeCount(Long productId);

    void increaseSalesCount(Long productId, int quantity);

    void decreaseSalesCount(Long productId, int quantity);

    void increaseViewCount(Long productId);
}
