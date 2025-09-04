package com.loopers.interfaces.event.listener;

import com.loopers.domain.event.dto.StockDecreaseEvent;
import com.loopers.domain.event.dto.StockIncreaseEvent;
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
public class StockEventHandler {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${kafka.topics.product}")
    private String productTopic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishStockEvent(StockIncreaseEvent event) {
        KafkaMessage<StockIncreaseEvent> message = KafkaMessage.of(event, "STOCK_INCREASE");
        kafkaTemplate.send(productTopic, event.getProductId(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", productTopic, message);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishStockEvent(StockDecreaseEvent event) {
        KafkaMessage<StockDecreaseEvent> message = KafkaMessage.of(event, "STOCK_DECREASE");
        kafkaTemplate.send(productTopic, event.getProductId(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", productTopic, message);
    }
}
