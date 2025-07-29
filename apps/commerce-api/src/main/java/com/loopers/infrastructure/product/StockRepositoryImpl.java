package com.loopers.infrastructure.product;

import com.loopers.domain.product.Stock;
import com.loopers.domain.product.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository stockJpaRepository;

    @Override
    public Stock findByProductId(Long productId) {
        return stockJpaRepository.findByProductId(productId).orElse(null);
    }

    @Override
    public List<Stock> findAllByProductIds(List<Long> productIds) {
        return stockJpaRepository.findAllByProductIdIn(productIds);
    }
}
