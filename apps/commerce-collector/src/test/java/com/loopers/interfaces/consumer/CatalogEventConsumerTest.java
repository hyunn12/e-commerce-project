package com.loopers.interfaces.consumer;

import com.loopers.domain.ProductMetricsCount;
import com.loopers.domain.ProductMetricsService;
import com.loopers.kafka.dto.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.Acknowledgment;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
class CatalogEventConsumerTest {

    @Mock
    private final ProductMetricsService productMetricsService = mock(ProductMetricsService.class);
    private final Acknowledgment ack = mock(Acknowledgment.class);

    private CatalogEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new CatalogEventConsumer(productMetricsService);
    }

    private ConsumerRecord<String, KafkaMessage<?>> recordOf(String type, Map<String, Object> payload) {
        KafkaMessage<Map<String, Object>> message = KafkaMessage.of(payload, type);
        return new ConsumerRecord<>("catalog-events", 0, 0, null, message);
    }

    @Nested
    class 이벤트_타입별_분기 {

        @Test
        @DisplayName("LIKE_ADD 이벤트면, likeCount=1 로 bulkUpdate 된다")
        void likeAddEvent() {
            Long productId = 1L;
            ConsumerRecord<String, KafkaMessage<?>> record = recordOf("LIKE_ADD", Map.of("productId", productId));

            consumer.consume(List.of(record), ack);

            ArgumentCaptor<Map<Long, ProductMetricsCount>> captor = ArgumentCaptor.forClass(Map.class);
            verify(productMetricsService).bulkUpdate(captor.capture());
            verify(ack).acknowledge();

            Map<Long, ProductMetricsCount> result = captor.getValue();
            ProductMetricsCount metrics = result.get(productId);
            assertThat(metrics.getLikeCount()).isEqualTo(1);
            assertThat(metrics.getSalesCount()).isEqualTo(0);
            assertThat(metrics.getViewCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("STOCK_INCREASE 이벤트면, salesCount=5 로 bulkUpdate 된다")
        void stockIncreaseEvent() {
            Long productId = 1L;
            ConsumerRecord<String, KafkaMessage<?>> record = recordOf("STOCK_INCREASE", Map.of("productId", productId, "quantity", 5));

            consumer.consume(List.of(record), ack);

            ArgumentCaptor<Map<Long, ProductMetricsCount>> captor = ArgumentCaptor.forClass(Map.class);
            verify(productMetricsService).bulkUpdate(captor.capture());
            verify(ack).acknowledge();

            ProductMetricsCount metrics = captor.getValue().get(productId);
            assertThat(metrics.getSalesCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("여러 이벤트가 한 productId로 들어오면 합산된다")
        void multipleEventsAggregated() {
            Long productId = 1L;
            ConsumerRecord<String, KafkaMessage<?>> r1 = recordOf("LIKE_ADD", Map.of("productId", productId));
            ConsumerRecord<String, KafkaMessage<?>> r2 = recordOf("LIKE_ADD", Map.of("productId", productId));
            ConsumerRecord<String, KafkaMessage<?>> r3 = recordOf("PRODUCT_VIEW", Map.of("productId", productId));

            consumer.consume(List.of(r1, r2, r3), ack);

            ArgumentCaptor<Map<Long, ProductMetricsCount>> captor = ArgumentCaptor.forClass(Map.class);
            verify(productMetricsService).bulkUpdate(captor.capture());
            verify(ack).acknowledge();

            ProductMetricsCount metrics = captor.getValue().get(productId);
            assertThat(metrics.getLikeCount()).isEqualTo(2);
            assertThat(metrics.getViewCount()).isEqualTo(1);
        }
    }
}
