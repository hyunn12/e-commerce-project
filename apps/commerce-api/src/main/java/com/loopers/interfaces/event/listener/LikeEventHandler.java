package com.loopers.interfaces.event.listener;

import com.loopers.domain.event.dto.CouponUseEvent;
import com.loopers.domain.event.dto.LikeAddEvent;
import com.loopers.domain.event.dto.LikeDeleteEvent;
import com.loopers.domain.product.ProductService;
import com.loopers.interfaces.event.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventHandler {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.catalog}")
    private String catalogTopic;

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

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishLikeAddEvent(LikeAddEvent event) {
        KafkaMessage<LikeAddEvent> message = KafkaMessage.of(event);
        kafkaTemplate.send(catalogTopic, event.getProductId().toString(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", catalogTopic, message);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishLikeDeleteEvent(LikeDeleteEvent event) {
        KafkaMessage<LikeDeleteEvent> message = KafkaMessage.of(event);
        kafkaTemplate.send(catalogTopic, event.getProductId().toString(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", catalogTopic, message);
    }
}
