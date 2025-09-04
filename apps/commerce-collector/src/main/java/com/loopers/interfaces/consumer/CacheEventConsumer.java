package com.loopers.interfaces.consumer;

import com.loopers.interfaces.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheEventConsumer {

    // cache event
    @KafkaListener(
            topics = "${kafka.topics.catalog}",
            groupId = "${kafka.consumer.group-id.cache}"
    )
    public void consume(ConsumerRecord<String, KafkaMessage<?>> record) {
        log.info("Catalog Event Published ::: {}", record.key());
        KafkaMessage<?> message = record.value();

    }

}
