package com.loopers.interfaces.consumer;

import com.loopers.domain.BrandCacheEvictService;
import com.loopers.interfaces.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheEventConsumer {

    private final BrandCacheEvictService brandCacheEvictService;

    @SuppressWarnings("unchecked")
    @KafkaListener(
            topics = "${kafka.topics.cache}",
            groupId = "${kafka.consumer.group-id.brand-cache}"
    )
    public void consume(ConsumerRecord<String, KafkaMessage<?>> record) {
        log.info("ConsumerRecord Received: {}", record.value());
        KafkaMessage<?> message = record.value();

        Map<String, Object> payload = (Map<String, Object>) message.payload();
        Long brandId = ((Number) payload.get("brandId")).longValue();
        log.info("Evict Cache Event for BrandId: {}", brandId);

        brandCacheEvictService.evict(brandId);
    }
}
