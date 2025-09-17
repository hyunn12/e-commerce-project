package com.loopers.infrastructure.ranking;

import com.loopers.application.ranking.RankingRaw;
import com.loopers.domain.ranking.RankingKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.loopers.redis.config.CacheConstants.RANKING_CACHE_TTL;
import static com.loopers.redis.config.CacheConstants.RANKING_PRODUCT_CACHE_MEMBER_KEY;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RankingRedisRepository {

    private final StringRedisTemplate redisTemplate;

    public List<RankingRaw> getTopRankings(String key, Pageable pageable) {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, pageable.getOffset(), pageable.getOffset() + pageable.getPageSize() - 1);
        if (tuples == null) return List.of();

        return tuples.stream()
                .map(tuple -> new RankingRaw(
                        tuple.getValue() == null ? null : Long.parseLong(tuple.getValue().substring(RANKING_PRODUCT_CACHE_MEMBER_KEY.length())),
                        tuple.getScore() == null ? 0.0 : tuple.getScore()
                ))
                .toList();
    }

    public long getTotalRankingCount(String key) {
        Long count = redisTemplate.opsForZSet().zCard(key);
        return count != null ? count : 0;
    }

    public Long getProductRanking(Long productId, String key) {
        return redisTemplate.opsForZSet().reverseRank(key, RankingKeyGenerator.buildMemberKey(productId));
    }

    public boolean existsRankingKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void warmUpTomorrow(String tomorrowKey, List<RankingRaw> raws, double ratio) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection conn = (StringRedisConnection) connection;
            for (RankingRaw raw : raws) {
                if (raw.productId() == null) continue;
                conn.zAdd(tomorrowKey, raw.score() * ratio, RankingKeyGenerator.buildMemberKey(raw.productId()));
            }
            return null;
        });

        redisTemplate.expire(tomorrowKey, RANKING_CACHE_TTL);
    }
}
