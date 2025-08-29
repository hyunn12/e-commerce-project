package com.loopers.interfaces.event.listener;

import com.loopers.domain.event.dto.LikeAddEvent;
import com.loopers.domain.event.dto.LikeDeleteEvent;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventHandler {

    private final ProductService productService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeAdded(LikeAddEvent event) {
        productService.increaseLike(event.getProductId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeDeleted(LikeDeleteEvent event) {
        productService.decreaseLike(event.getProductId());
    }
}
