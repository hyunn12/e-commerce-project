package com.loopers.kafka.config;

import com.loopers.kafka.dto.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

public final class KafkaPayloadExtractor {

    private KafkaPayloadExtractor() {}

    @SuppressWarnings("unchecked")
    public static KafkaMessage<Map<String, Object>> extract(ConsumerRecord<String, KafkaMessage<?>> record) {
        KafkaMessage<?> message = record.value();
        return (KafkaMessage<Map<String, Object>>) message;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(Map<String, Object> payload, String key, Class<T> type) {
        Object value = payload.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            if (type == Long.class && value instanceof Number number) {
                return (T) Long.valueOf(number.longValue());
            }
            if (type == Integer.class && value instanceof Number number) {
                return (T) Integer.valueOf(number.intValue());
            }
            throw new IllegalArgumentException(
                "Expected type " + type.getName() + " for key '" + key + "', but got " + value.getClass().getName()
            );
        }
        return (T) value;
    }
}
