package com.loopers.application.order;

import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductService productService;

    public void decrease(Long productId, Integer quantity) {
        productService.decreaseStockWithLock(productId, quantity);
    }

    public void increase(Long productId, Integer quantity) {
        productService.increaseStockWithLock(productId, quantity);
    }
}
