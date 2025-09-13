package com.loopers.interfaces.consumer;

import com.loopers.domain.cache.BrandCacheEvictService;
import com.loopers.kafka.dto.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.Acknowledgment;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
class CacheEventConsumerTest {

    private final BrandCacheEvictService brandCacheEvictService = mock(BrandCacheEvictService.class);
    private final Acknowledgment ack = mock(Acknowledgment.class);

    private final CacheEventConsumer consumer = new CacheEventConsumer(brandCacheEvictService);

    @Test
    @DisplayName("브랜드 캐시 무효화 이벤트를 수신하면, 해당 brandId의 캐시를 삭제한다.")
    void consumeMessage_evictBrandCache() {
        // arrange
        Long brandId = 1L;
        Map<String, Object> payload = Map.of("brandId", brandId);
        KafkaMessage<Map<String, Object>> message = KafkaMessage.of(payload, "BRAND_MODIFY");

        ConsumerRecord<String, KafkaMessage<?>> record =
                new ConsumerRecord<>("cache-events", 0, 0, null, message);

        // act
        consumer.consume(List.of(record), ack);

        // assert
        verify(brandCacheEvictService).evict(brandId);
        verify(ack).acknowledge();
    }

}
