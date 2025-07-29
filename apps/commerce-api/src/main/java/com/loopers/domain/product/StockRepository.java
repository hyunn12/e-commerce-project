package com.loopers.domain.product;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository {

    Stock findByProductId(Long productId);

    List<Stock> findAllByProductIds(List<Long> productIds);
}
