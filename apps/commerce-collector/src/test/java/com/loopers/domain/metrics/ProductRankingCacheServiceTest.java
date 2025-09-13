package com.loopers.domain.metrics;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.loopers.redis.config.CacheConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRankingCacheServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ProductRankingWeightProperties weight;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private ProductRankingCacheService productRankingCacheService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(RANKING_DATE_PATTERN);

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        // Weight 설정
        lenient().when(weight.getLike()).thenReturn(3.0);
        lenient().when(weight.getView()).thenReturn(1.0);
        lenient().when(weight.getSales()).thenReturn(5.0);
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 랭킹_업데이트_시 {

        @DisplayName("빈 데이터가 주어진다면 아무것도 하지 않는다.")
        @Test
        void doNothing_whenEmptyMetricsGiven() {
            // arrange
            Map<Long, ProductMetricsCount> emptyAggregate = new HashMap<>();

            // act
            productRankingCacheService.updateRanking(emptyAggregate);

            // assert
            verifyNoInteractions(redisTemplate);
        }

        @DisplayName("유효한 집계 데이터가 주어진다면 Redis에 랭킹을 업데이트한다.")
        @Test
        void updateRedisRanking_whenValidMetricsGiven() {
            // arrange
            Long productId1 = 1L;
            Long productId2 = 2L;
            Map<Long, ProductMetricsCount> aggregate = new HashMap<>();
            aggregate.put(productId1, new ProductMetricsCount(10, 100, 5));
            aggregate.put(productId2, new ProductMetricsCount(5, 50, 2));

            String expectedKey = RANKING_PRODUCT_CACHE_KEY_PREFIX + ZonedDateTime.now().format(FORMATTER);
            given(redisTemplate.hasKey(expectedKey)).willReturn(false);

            // act
            productRankingCacheService.updateRanking(aggregate);

            // assert
            verify(zSetOperations).incrementScore(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId1, 155.0);
            verify(zSetOperations).incrementScore(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId2, 75.0);
            verify(redisTemplate).expire(expectedKey, Duration.ofDays(2));
        }

        @DisplayName("이미 키가 존재한다면 TTL을 설정하지 않는다.")
        @Test
        void notSetTTL_whenKeyExists() {
            // arrange
            Long productId = 1L;
            Map<Long, ProductMetricsCount> aggregate = new HashMap<>();
            aggregate.put(productId, new ProductMetricsCount(1, 1, 1));

            String expectedKey = RANKING_PRODUCT_CACHE_KEY_PREFIX + ZonedDateTime.now().format(FORMATTER);
            given(redisTemplate.hasKey(expectedKey)).willReturn(true);

            // act
            productRankingCacheService.updateRanking(aggregate);

            // assert
            verify(zSetOperations).incrementScore(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId, 9.0);
            verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
        }

        @DisplayName("여러 상품의 집계가 주어진다면 점수가 바르게 계산된다.")
        @Test
        void calculateCorrectScores_whenProductsMetricsGiven() {
            // arrange
            Long productId1 = 1L;
            Long productId2 = 2L;
            Long productId3 = 3L;

            Map<Long, ProductMetricsCount> aggregate = new HashMap<>();
            aggregate.put(productId1, new ProductMetricsCount(20, 200, 10));
            aggregate.put(productId2, new ProductMetricsCount(0, 50, 3));
            aggregate.put(productId3, new ProductMetricsCount(15, 0, 0));

            String expectedKey = RANKING_PRODUCT_CACHE_KEY_PREFIX + ZonedDateTime.now().format(FORMATTER);
            given(redisTemplate.hasKey(expectedKey)).willReturn(false);

            // act
            productRankingCacheService.updateRanking(aggregate);

            // assert
            verify(zSetOperations).incrementScore(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId1, 310.0);
            verify(zSetOperations).incrementScore(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId2, 65.0);
            verify(zSetOperations).incrementScore(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId3, 45.0);
            verify(redisTemplate).expire(expectedKey, Duration.ofDays(2));
        }
    }
}
