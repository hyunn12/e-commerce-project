package com.loopers.domain.metrics;

import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.loopers.redis.config.CacheConstants.*;
import static com.loopers.redis.config.CacheConstants.RANKING_PRODUCT_CACHE_MEMBER_KEY;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ProductRankingCacheServiceIntegrationTest {

    @Autowired
    private ProductRankingCacheService productRankingCacheService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisCleanUp redisCleanUp;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(RANKING_DATE_PATTERN);

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 랭킹_업데이트_시 {

        @DisplayName("상품 데이터가 주어진다면 Redis에 올바르게 저장된다.")
        @Test
        void storeCorrectlyInRedis_whenMetricsGiven() {
            // arrange
            Long productId1 = 1L;
            Long productId2 = 2L;
            Long productId3 = 3L;

            Map<Long, ProductMetricsCount> aggregate = new HashMap<>();
            aggregate.put(productId1, new ProductMetricsCount(10, 100, 5));
            aggregate.put(productId2, new ProductMetricsCount(5, 50, 2));
            aggregate.put(productId3, new ProductMetricsCount(15, 80, 8));

            String expectedKey = RANKING_PRODUCT_CACHE_KEY_PREFIX + ZonedDateTime.now().format(FORMATTER);

            // act
            productRankingCacheService.updateRanking(aggregate);

            // assert
            assertThat(redisTemplate.hasKey(expectedKey)).isTrue();

            Set<String> members = redisTemplate.opsForZSet().range(expectedKey, 0, -1);
            assertThat(members).hasSize(3);
            assertThat(members).containsExactlyInAnyOrder(RANKING_PRODUCT_CACHE_MEMBER_KEY + productId1, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId2, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId3);

            Double score1 = redisTemplate.opsForZSet().score(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId1);
            Double score2 = redisTemplate.opsForZSet().score(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId2);
            Double score3 = redisTemplate.opsForZSet().score(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId3);

            assertThat(score1).isNotNull().isPositive();
            assertThat(score2).isNotNull().isPositive();
            assertThat(score3).isNotNull().isPositive();
        }

        @DisplayName("동일 상품의 데이터가 여러 번 주어진다면 점수가 누적된다.")
        @Test
        void accumulateScores_whenSameProductMetricsGiven() {
            // arrange
            Long productId = 1L;

            Map<Long, ProductMetricsCount> firstAggregate = new HashMap<>();
            firstAggregate.put(productId, new ProductMetricsCount(5, 50, 2));

            Map<Long, ProductMetricsCount> secondAggregate = new HashMap<>();
            secondAggregate.put(productId, new ProductMetricsCount(3, 30, 1));

            String expectedKey = RANKING_PRODUCT_CACHE_KEY_PREFIX + ZonedDateTime.now().format(FORMATTER);

            // act
            productRankingCacheService.updateRanking(firstAggregate);
            Double firstScore = redisTemplate.opsForZSet().score(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId);

            productRankingCacheService.updateRanking(secondAggregate);
            Double finalScore = redisTemplate.opsForZSet().score(expectedKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId);

            // assert
            assertThat(firstScore).isNotNull();
            assertThat(finalScore).isNotNull();
            assertThat(finalScore).isGreaterThan(firstScore);
        }

        @DisplayName("여러 날짜의 랭킹 데이터가 있다면 키가 독립적으로 관리된다")
        @Test
        void manageMultipleDateKeys() {
            // arrange
            Long productId1 = 1L;
            Long productId2 = 2L;

            String todayKey = RANKING_PRODUCT_CACHE_KEY_PREFIX + ZonedDateTime.now().format(FORMATTER);
            String manualKey = RANKING_PRODUCT_CACHE_KEY_PREFIX + "20240101";

            Map<Long, ProductMetricsCount> aggregate = new HashMap<>();
            aggregate.put(productId1, new ProductMetricsCount(1, 1, 1));

            redisTemplate.opsForZSet().add(manualKey, RANKING_PRODUCT_CACHE_MEMBER_KEY + productId2, 100.0);

            // act
            productRankingCacheService.updateRanking(aggregate);

            // assert
            assertThat(redisTemplate.hasKey(todayKey)).isTrue();
            assertThat(redisTemplate.hasKey(manualKey)).isTrue();

            Set<String> todayMembers = redisTemplate.opsForZSet().range(todayKey, 0, -1);
            Set<String> manualMembers = redisTemplate.opsForZSet().range(manualKey, 0, -1);

            assertThat(todayMembers).containsExactly(RANKING_PRODUCT_CACHE_MEMBER_KEY + productId1);
            assertThat(manualMembers).containsExactly(RANKING_PRODUCT_CACHE_MEMBER_KEY + productId2);
        }

        @DisplayName("빈 데이터가 주어진다면 Redis에 아무것도 저장되지 않는다")
        @Test
        void notCreateAnyKeys_whenEmptyMetricsGiven() {
            // arrange
            Map<Long, ProductMetricsCount> emptyAggregate = new HashMap<>();
            String expectedKey = RANKING_PRODUCT_CACHE_KEY_PREFIX + ZonedDateTime.now().format(FORMATTER);

            // act
            productRankingCacheService.updateRanking(emptyAggregate);

            // assert
            assertThat(redisTemplate.hasKey(expectedKey)).isFalse();
        }
    }
}
