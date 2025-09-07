package com.loopers.interfaces.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.EventHandledService;
import com.loopers.domain.EventLog;
import com.loopers.domain.EventLogService;
import com.loopers.kafka.dto.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventLogConsumerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EventLogService eventLogService = mock(EventLogService.class);
    private final EventHandledService eventHandledService = mock(EventHandledService.class);
    private final Acknowledgment ack = mock(Acknowledgment.class);

    private final EventLogConsumer consumer =
            new EventLogConsumer(objectMapper, eventLogService, eventHandledService);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(consumer, "groupId", "event-logs");
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 이벤트_수신_시 {

        @DisplayName("새로운 이벤트라면, EventLog 를 저장한다.")
        @Test
        void whenNewEvent_thenSaveEventLog() throws JsonProcessingException {
            // arrange
            Map<String, Object> payload = Map.of("productId", 1L, "userId", 1L);
            KafkaMessage<Map<String, Object>> message = KafkaMessage.of(payload, "LIKE_ADD");

            ConsumerRecord<String, KafkaMessage<?>> record =
                    new ConsumerRecord<>("catalog-events", 0, 0, null, message);

            when(eventHandledService.markHandled(anyString(), anyString())).thenReturn(true);

            // act
            consumer.consume(List.of(record), ack);

            // assert
            ArgumentCaptor<EventLog> captor = ArgumentCaptor.forClass(EventLog.class);
            verify(eventLogService).save(captor.capture());

            EventLog saved = captor.getValue();
            assertThat(saved.getEventId()).isEqualTo(message.eventId());
        }

        @DisplayName("중복 이벤트라면, EventLog 를 저장하지 않는다.")
        @Test
        void whenDuplicateEvent_thenSkipSaveEventLog() throws JsonProcessingException {
            // arrange
            Map<String, Object> payload = Map.of("productId", 1L, "userId", 1L);
            KafkaMessage<Map<String, Object>> message = KafkaMessage.of(payload, "LIKE_ADD");

            ConsumerRecord<String, KafkaMessage<?>> record =
                    new ConsumerRecord<>("topic", 0, 0, null, message);

            when(eventHandledService.markHandled(anyString(), anyString())).thenReturn(false);

            // act
            consumer.consume(List.of(record), ack);

            // assert
            verify(eventLogService, never()).save(any());
            verify(ack).acknowledge();
        }
    }
}
