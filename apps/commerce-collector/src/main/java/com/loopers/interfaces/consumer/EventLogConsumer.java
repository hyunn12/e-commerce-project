package com.loopers.interfaces.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.EventHandledService;
import com.loopers.domain.EventLog;
import com.loopers.domain.EventLogService;
import com.loopers.interfaces.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventLogConsumer {

    private final ObjectMapper objectMapper;
    private final EventLogService eventLogService;
    private final EventHandledService eventHandledService;

    @Value("${kafka.consumer.group-id.event}")
    private String groupId;

    @KafkaListener(
            topics = {"${kafka.topics.catalog}", "${kafka.topics.product}", "${kafka.topics.order}", "${kafka.topics.user}"},
            groupId = "${kafka.consumer.group-id.event}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consume(List<ConsumerRecord<String, KafkaMessage<?>>> records, Acknowledgment ack) throws JsonProcessingException {
        for(ConsumerRecord<String, KafkaMessage<?>> record : records) {
            log.info("ConsumerRecord Received: {}", record.value());
            KafkaMessage<?> message = record.value();

            if (!eventHandledService.markHandled(message.eventId(), groupId)) {
                log.info("Duplicate EventId: {}", message.eventId());
                continue;
            }

            String payloadJson = objectMapper.writeValueAsString(message.payload());
            EventLog eventLog = EventLog.of(message.eventId(), payloadJson, message.version(), message.publishedAt());
            eventLogService.save(eventLog);
        }

        ack.acknowledge();
    }
}
