package com.loopers.interfaces.consumer;

import com.loopers.domain.metrics.ProductRankingCacheService;
import com.loopers.domain.metrics.ProductMetricsCount;
import com.loopers.domain.metrics.ProductMetricsService;
import com.loopers.kafka.config.KafkaConfig;
import com.loopers.kafka.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.loopers.kafka.config.KafkaPayloadExtractor.getValue;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductMetricsConsumer {

    private final ProductMetricsService productMetricsService;
    private final ProductRankingCacheService productRankingCacheService;

    @SuppressWarnings("unchecked")
    @KafkaListener(
            topics = {"${kafka.topics.catalog}", "${kafka.topics.product}"},
            groupId = "${kafka.consumer.group-id.metrics}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consume(List<ConsumerRecord<String, KafkaMessage<?>>> records, Acknowledgment ack) {
        Map<Long, ProductMetricsCount> aggregate = new HashMap<>();

        for (ConsumerRecord<String, KafkaMessage<?>> record : records) {
            KafkaMessage<?> message = record.value();
            Map<String, Object> payload = (Map<String, Object>) message.payload();

            Long productId = getValue(payload, "productId", Long.class);
            Integer quantity = getValue(payload, "quantity", Integer.class);

            aggregate.computeIfAbsent(productId, id -> new ProductMetricsCount())
                    .apply(message.type(), quantity != null ? quantity : 0);
        }

        if (!aggregate.isEmpty()) {
            productMetricsService.bulkUpdate(aggregate);
            productRankingCacheService.updateRanking(aggregate);
        }

        ack.acknowledge();
    }
}
