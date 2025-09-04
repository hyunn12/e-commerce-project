package com.loopers.interfaces.consumer;

import com.loopers.domain.ProductMetricsService;
import com.loopers.interfaces.dto.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.Acknowledgment;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

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
        @DisplayName("LIKE_ADD 이벤트면, likeCount를 증가시킨다")
        void likeAddEvent() {
            // arrange
            Long productId = 1L;
            ConsumerRecord<String, KafkaMessage<?>> record =
                    recordOf("LIKE_ADD", Map.of("productId", productId));

            // act
            consumer.consume(List.of(record), ack);

            // assert
            verify(productMetricsService).increaseLikeCount(productId);
            verify(ack).acknowledge();
        }

        @Test
        @DisplayName("LIKE_DELETE 이벤트면, likeCount를 감소시킨다")
        void likeDeleteEvent() {
            Long productId = 2L;
            ConsumerRecord<String, KafkaMessage<?>> record =
                    recordOf("LIKE_DELETE", Map.of("productId", productId));

            consumer.consume(List.of(record), ack);

            verify(productMetricsService).decreaseLikeCount(productId);
            verify(ack).acknowledge();
        }

        @Test
        @DisplayName("STOCK_INCREASE 이벤트면, salesCount를 증가시킨다")
        void stockIncreaseEvent() {
            Long productId = 3L;
            ConsumerRecord<String, KafkaMessage<?>> record =
                    recordOf("STOCK_INCREASE", Map.of("productId", productId, "quantity", 5));

            consumer.consume(List.of(record), ack);

            verify(productMetricsService).increaseSalesCount(productId, 5);
            verify(ack).acknowledge();
        }

        @Test
        @DisplayName("STOCK_DECREASE 이벤트면, salesCount를 감소시킨다")
        void stockDecreaseEvent() {
            Long productId = 4L;
            ConsumerRecord<String, KafkaMessage<?>> record =
                    recordOf("STOCK_DECREASE", Map.of("productId", productId, "quantity", 2));

            consumer.consume(List.of(record), ack);

            verify(productMetricsService).decreaseSalesCount(productId, 2);
            verify(ack).acknowledge();
        }

        @Test
        @DisplayName("PRODUCT_VIEW 이벤트면, viewCount를 증가시킨다")
        void productViewEvent() {
            Long productId = 1L;
            ConsumerRecord<String, KafkaMessage<?>> record =
                    recordOf("PRODUCT_VIEW", Map.of("productId", productId));

            consumer.consume(List.of(record), ack);

            verify(productMetricsService).increaseViewCount(productId);
            verify(ack).acknowledge();
        }
    }
//    @Nested
//    class 이벤트_수신_시 {
//
//        @Test
//        @DisplayName("LIKE_ADD 이벤트라면, 해당 상품의 likeCount를 증가시킨다.")
//        void consumeMessage_increaseLikeCount() {
//            // arrange
//            Long productId = 1L;
//            Map<String, Object> payload = Map.of("productId", productId, "userId", 1L);
//            KafkaMessage<Map<String, Object>> message = KafkaMessage.of(payload, "LIKE_ADD");
//
//            ConsumerRecord<String, KafkaMessage<?>> record =
//                    new ConsumerRecord<>("catalog-events", 0, 0, null, message);
//
//            // act
//            consumer.consume(List.of(record), ack);
//
//            // assert
//            verify(productMetricsService).increaseLikeCount(productId);
//            verify(ack).acknowledge();
//        }
//    }
}
