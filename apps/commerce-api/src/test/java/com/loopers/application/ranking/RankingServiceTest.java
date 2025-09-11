package com.loopers.application.ranking;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

import static com.loopers.redis.config.CacheConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private RankingService rankingService;

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 랭킹_조회_시 {

        private final String key = RANKING_PRODUCT_CACHE_KEY_PREFIX + "20250911";

        @BeforeEach
        void setUp() {
            given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
        }

        @DisplayName("정상적으로 랭킹 데이터를 반환한다.")
        @Test
        void returnRankingData() {
            // arrange
            Long productId1 = 1L;
            Long productId2 = 2L;
            double score1 = 99.9;
            double score2 = 88.8;

            Pageable pageable = PageRequest.of(0, 5);
            
            ZSetOperations.TypedTuple<String> tuple1 = mock(ZSetOperations.TypedTuple.class);
            ZSetOperations.TypedTuple<String> tuple2 = mock(ZSetOperations.TypedTuple.class);
            given(tuple1.getValue()).willReturn(RANKING_PRODUCT_CACHE_MEMBER_KEY + productId1);
            given(tuple1.getScore()).willReturn(score1);
            given(tuple2.getValue()).willReturn(RANKING_PRODUCT_CACHE_MEMBER_KEY + productId2);
            given(tuple2.getScore()).willReturn(score2);
            
            Set<ZSetOperations.TypedTuple<String>> tuples = Set.of(tuple1, tuple2);
            given(zSetOperations.reverseRangeWithScores(key, 0, 4)).willReturn(tuples);

            // act
            List<RankingRaw> result = rankingService.getTopRankings(key, pageable);

            // assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(RankingRaw::productId).containsExactlyInAnyOrder(productId1, productId2);
            assertThat(result).extracting(RankingRaw::score).containsExactlyInAnyOrder(score1, score2);
        }

        @DisplayName("Redis에서 null을 반환하면 빈 리스트를 반환한다.")
        @Test
        void returnEmptyList_whenRedisReturnsNull() {
            // arrange
            Pageable pageable = PageRequest.of(0, 5);
            given(zSetOperations.reverseRangeWithScores(key, 0, 4)).willReturn(null);

            // act
            List<RankingRaw> result = rankingService.getTopRankings(key, pageable);

            // assert
            assertThat(result).isEmpty();
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 전체_랭킹_수_조회_시 {

        private final String key = RANKING_PRODUCT_CACHE_KEY_PREFIX + "20250911";

        @BeforeEach
        void setUp() {
            given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
        }

        @DisplayName("정상적으로 전체 수를 반환한다.")
        @Test
        void returnTotalCountSuccessfully() {
            // arrange
            long count = 100L;
            given(zSetOperations.zCard(key)).willReturn(count);

            // act
            long result = rankingService.getTotalRankingCount(key);

            // assert
            assertThat(result).isEqualTo(count);
        }

        @DisplayName("Redis에서 null을 반환하면 0을 반환한다.")
        @Test
        void returnZeroWhenRedisReturnsNull() {
            // arrange
            given(zSetOperations.zCard(key)).willReturn(null);

            // act
            long result = rankingService.getTotalRankingCount(key);

            // assert
            assertThat(result).isEqualTo(0L);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 랭킹_키_존재_여부_확인_시 {

        private final String key = RANKING_PRODUCT_CACHE_KEY_PREFIX + "20250911";

        @DisplayName("키가 존재하면 true를 반환한다.")
        @Test
        void returnTrueWhenKeyExists() {
            // arrange
            given(redisTemplate.hasKey(key)).willReturn(true);

            // act
            boolean result = rankingService.existsRankingKey(key);

            // assert
            assertThat(result).isTrue();
        }

        @DisplayName("키가 존재하지 않으면 false를 반환한다.")
        @Test
        void returnFalseWhenKeyDoesNotExist() {
            // arrange
            given(redisTemplate.hasKey(key)).willReturn(false);

            // act
            boolean result = rankingService.existsRankingKey(key);

            // assert
            assertThat(result).isFalse();
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 랭킹_키_생성_시 {

        @DisplayName("날짜 문자열로 키를 생성한다.")
        @Test
        void buildCorrectKeyWithDateString() {
            // arrange
            String date = "20250911";

            // act
            String result = rankingService.buildRankingKey(date);

            // assert
            assertThat(result).isEqualTo(RANKING_PRODUCT_CACHE_KEY_PREFIX + "20250911");
        }

        @DisplayName("잘못된 날짜 문자열이라면 400 Bad Request 예외가 발생한다.")
        @Test
        void buildKeyWithEmptyString() {
            // arrange
            String date = "12345678";

            // act
            CoreException exception = assertThrows(CoreException.class, () -> rankingService.buildRankingKey(date));

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
