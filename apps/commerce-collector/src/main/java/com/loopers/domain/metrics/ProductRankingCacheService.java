package com.loopers.domain.metrics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.loopers.redis.config.CacheConstants.*;

@Service
@RequiredArgsConstructor
public class ProductRankingCacheService {


    private final StringRedisTemplate redisTemplate;
    private final ProductRankingWeightProperties weight;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(RANKING_DATE_PATTERN);

    public void updateRanking(Map<Long, ProductMetricsCount> aggregate) {
        if (aggregate.isEmpty()) return;

        String key = RANKING_PRODUCT_CACHE_KEY_PREFIX + ZonedDateTime.now().format(FORMATTER);

        for (Map.Entry<Long, ProductMetricsCount> entry : aggregate.entrySet()) {
            Long productId = entry.getKey();
            ProductMetricsCount count = entry.getValue();

            double score = calculateScore(count);
            if (score <= 0) continue;

            String member = RANKING_PRODUCT_CACHE_MEMBER_KEY + productId;

            // score 누적
            redisTemplate.opsForZSet().incrementScore(key, member, score);
        }

        // TTL 설정
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.expire(key, RANKING_CACHE_TTL);
        }
    }

    /**
     * ProductMetricsCount 기반 점수 계산
     */
    private double calculateScore(ProductMetricsCount count) {
        double score = 0;

        score += count.getLikeCount() * weight.getLike();
        score += count.getViewCount() * weight.getView();
        score += count.getSalesCount() * weight.getSales();

        return score;
    }
}
