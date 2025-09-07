package com.loopers.interfaces.consumer;

import com.loopers.domain.BrandCacheEvictService;
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
public class CacheEventConsumer {

    private final BrandCacheEvictService brandCacheEvictService;

    @SuppressWarnings("unchecked")
    @KafkaListener(
            topics = "${kafka.topics.cache}",
            groupId = "${kafka.consumer.group-id.brand-cache}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consume(List<ConsumerRecord<String, KafkaMessage<?>>> records, Acknowledgment ack) {
        for (ConsumerRecord<String, KafkaMessage<?>> record : records) {
            log.info("ConsumerRecord Received: {}", record.value());
            KafkaMessage<?> message = record.value();

            Map<String, Object> payload = (Map<String, Object>) message.payload();
            Long brandId = ((Number) payload.get("brandId")).longValue();
            log.info("Evict Cache Event for BrandId: {}", brandId);

            brandCacheEvictService.evict(brandId);
        }
        ack.acknowledge();
    }
}
