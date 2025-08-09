package com.loopers.application.order;

import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockDecreaseService {

    private final ProductService productService;

    public void decrease(Long productId, Integer quantity) {
        productService.decreaseStockWithLock(productId, quantity);
    }
}
