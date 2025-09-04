package com.loopers.interfaces.event.listener;

import com.loopers.domain.event.dto.BrandModifyEvent;
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
public class BrandEventHandler {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${kafka.topics.cache}")
    private String cacheTopic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBrandModify(BrandModifyEvent event) {
        KafkaMessage<BrandModifyEvent> message = KafkaMessage.of(event, "BRAND_MODIFY");
        kafkaTemplate.send(cacheTopic, event.getBrandId(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", cacheTopic, message);
    }
}
