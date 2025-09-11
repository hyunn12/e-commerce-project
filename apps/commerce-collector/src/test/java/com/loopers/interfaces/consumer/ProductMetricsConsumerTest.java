package com.loopers.interfaces.consumer;

import com.loopers.domain.metrics.ProductMetricsCount;
import com.loopers.domain.metrics.ProductMetricsService;
import com.loopers.domain.metrics.ProductRankingCacheService;
import com.loopers.kafka.dto.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductMetricsConsumerTest {

    @Mock
    private ProductMetricsService productMetricsService;
    
    @Mock
    private ProductRankingCacheService productRankingCacheService;
    
    private final Acknowledgment ack = mock(Acknowledgment.class);

    @InjectMocks
    private ProductMetricsConsumer consumer;

    private ConsumerRecord<String, KafkaMessage<?>> recordOf(String type, Map<String, Object> payload) {
        KafkaMessage<Map<String, Object>> message = KafkaMessage.of(payload, type);
        return new ConsumerRecord<>("catalog-events", 0, 0, null, message);
    }

    private void verifyServices(ArgumentCaptor<Map<Long, ProductMetricsCount>> captor) {
        verify(productMetricsService).bulkUpdate(captor.capture());
        verify(productRankingCacheService).updateRanking(captor.capture());
        verify(ack).acknowledge();

        List<Map<Long, ProductMetricsCount>> allValues = captor.getAllValues();
        assertThat(allValues).hasSize(2);
        assertThat(allValues.get(0)).isEqualTo(allValues.get(1));
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 이벤트_타입별_분기 {

        @SuppressWarnings("unchecked")
        @DisplayName("LIKE_ADD 이벤트면, likeCount=1 로 집계된 후 서비스가 호출된다.")
        @Test
        void likeAddEvent() {
            // arrange
            Long productId = 1L;
            ConsumerRecord<String, KafkaMessage<?>> record = recordOf("LIKE_ADD", Map.of("productId", productId));
            ArgumentCaptor<Map<Long, ProductMetricsCount>> captor = ArgumentCaptor.forClass(Map.class);

            // act
            consumer.consume(List.of(record), ack);

            // assert
            verifyServices(captor);

            Map<Long, ProductMetricsCount> result = captor.getAllValues().get(0);
            ProductMetricsCount metrics = result.get(productId);

            assertThat(metrics.getLikeCount()).isEqualTo(1);
            assertThat(metrics.getSalesCount()).isEqualTo(0);
            assertThat(metrics.getViewCount()).isEqualTo(0);
        }

        @SuppressWarnings("unchecked")
        @DisplayName("STOCK_INCREASE 이벤트면, salesCount=5 로 집계된 후 서비스가 호출된다.")
        @Test
        void stockIncreaseEvent() {
            // arrange
            Long productId = 1L;
            ConsumerRecord<String, KafkaMessage<?>> record = recordOf("STOCK_INCREASE", Map.of("productId", productId, "quantity", 5));
            ArgumentCaptor<Map<Long, ProductMetricsCount>> captor = ArgumentCaptor.forClass(Map.class);

            // act
            consumer.consume(List.of(record), ack);

            // assert
            verifyServices(captor);

            ProductMetricsCount metrics = captor.getAllValues().get(0).get(productId);
            assertThat(metrics.getSalesCount()).isEqualTo(5);
            assertThat(metrics.getLikeCount()).isEqualTo(0);
            assertThat(metrics.getViewCount()).isEqualTo(0);
        }

        @SuppressWarnings("unchecked")
        @DisplayName("여러 이벤트가 한 productId로 들어오면 집계된 후 서비스가 호출된다.")
        @Test
        void multipleEventsAggregated() {
            // arrange
            Long productId = 1L;
            ConsumerRecord<String, KafkaMessage<?>> record1 = recordOf("LIKE_ADD", Map.of("productId", productId));
            ConsumerRecord<String, KafkaMessage<?>> record2 = recordOf("LIKE_ADD", Map.of("productId", productId));
            ConsumerRecord<String, KafkaMessage<?>> record3 = recordOf("PRODUCT_VIEW", Map.of("productId", productId));
            ArgumentCaptor<Map<Long, ProductMetricsCount>> captor = ArgumentCaptor.forClass(Map.class);

            // act
            consumer.consume(List.of(record1, record2, record3), ack);

            // assert
            verifyServices(captor);

            ProductMetricsCount metrics = captor.getAllValues().get(0).get(productId);
            assertThat(metrics.getLikeCount()).isEqualTo(2);
            assertThat(metrics.getViewCount()).isEqualTo(1);
            assertThat(metrics.getSalesCount()).isEqualTo(0);
        }
    }
}
