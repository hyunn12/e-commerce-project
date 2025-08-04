package com.loopers.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public Stock getDetailByProductId(Long productId) {
        return stockRepository.findByProductId(productId);
    }

    public List<Stock> getListByProductIds(List<Long> productIds) {
        return stockRepository.findAllByProductIds(productIds);
    }
}
