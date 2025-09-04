package com.loopers.interfaces.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.EventLog;
import com.loopers.domain.EventLogService;
import com.loopers.interfaces.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventLogConsumer {

    private final ObjectMapper objectMapper;
    private final EventLogService eventLogService;

    @KafkaListener(
            topics = {"${kafka.topics.catalog}", "${kafka.topics.product}", "${kafka.topics.order}", "${kafka.topics.user}"},
            groupId = "${kafka.consumer.group-id.event}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, KafkaMessage<?>> record, Acknowledgment ack) {
        try {
            log.info("ConsumerRecord Received: {}", record.value());
            KafkaMessage<?> message = record.value();

            if (eventLogService.existsByEventId(message.eventId())) {
                log.info("Duplicate EventId: {}", message.eventId());
                ack.acknowledge();
                return;
            }

            String payloadJson = objectMapper.writeValueAsString(message.payload());

            EventLog eventLog = EventLog.of(message.eventId(), payloadJson, message.version(), message.publishedAt());
            eventLogService.save(eventLog);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Event Log Save Failed: {}", e.getLocalizedMessage());
        }
    }
}
