package com.loopers.interfaces.consumer;

import com.loopers.domain.ProductMetricsService;
import com.loopers.kafka.config.KafkaConfig;
import com.loopers.kafka.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogEventConsumer {

    private final ProductMetricsService productMetricsService;

    @SuppressWarnings("unchecked")
    @KafkaListener(
            topics = {"${kafka.topics.catalog}", "${kafka.topics.product}"},
            groupId = "${kafka.consumer.group-id.metrics}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consume(List<ConsumerRecord<String, KafkaMessage<?>>> records, Acknowledgment ack) {
        for (ConsumerRecord<String, KafkaMessage<?>> record : records) {
            log.info("ConsumerRecord Received: {}", record.value());
            KafkaMessage<?> message = record.value();

            Map<String, Object> payload = (Map<String, Object>) message.payload();
            Long productId = ((Number) payload.get("productId")).longValue();
            int quantity = 0;
            if (payload.containsKey("quantity")) {
                quantity = ((Number) payload.get("quantity")).intValue();
            }

            // 이벤트 타입별 분기
            switch (message.type()) {
                case "LIKE_ADD" -> productMetricsService.increaseLikeCount(productId);
                case "LIKE_DELETE" -> productMetricsService.decreaseLikeCount(productId);
                case "STOCK_INCREASE" -> productMetricsService.increaseSalesCount(productId, quantity);
                case "STOCK_DECREASE" -> productMetricsService.decreaseSalesCount(productId, quantity);
                case "PRODUCT_VIEW" -> productMetricsService.increaseViewCount(productId);
                default -> log.warn("잘못된 이벤트 타입: {}", message.type());
            }
        }
        ack.acknowledge();
    }
}
