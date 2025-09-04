package com.loopers.application.order;

import com.loopers.domain.event.StockEventPublisher;
import com.loopers.domain.event.dto.StockDecreaseEvent;
import com.loopers.domain.event.dto.StockIncreaseEvent;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductService productService;
    private final StockEventPublisher stockEventPublisher;

    public void decrease(Long productId, Integer quantity) {
        productService.decreaseStockWithLock(productId, quantity);
        stockEventPublisher.publish(StockDecreaseEvent.of(productId, quantity));
    }

    public void increase(Long productId, Integer quantity) {
        productService.increaseStockWithLock(productId, quantity);
        stockEventPublisher.publish(StockIncreaseEvent.of(productId, quantity));
    }
}
