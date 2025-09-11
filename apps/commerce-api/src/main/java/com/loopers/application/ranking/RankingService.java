package com.loopers.application.ranking;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

import static com.loopers.redis.config.CacheConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final StringRedisTemplate redisTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(RANKING_DATE_PATTERN);

    @Value("${ranking.ratio}")
    private double ratio;

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

    public long getTotalRankingCount(String rankingKey) {
        Long count = redisTemplate.opsForZSet().zCard(rankingKey);
        return count != null ? count : 0;
    }

    public Long getProductRanking(Long productId, String rankingKey) {
        String member = buildMemberKey(productId);
        return redisTemplate.opsForZSet().reverseRank(rankingKey, member);
    }

    public boolean existsRankingKey(String rankingKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(rankingKey));
    }

    public String buildRankingKey(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date, FORMATTER);
            return buildRankingKey(parsedDate);
        } catch (DateTimeParseException e) {
            log.error("Error parsing date: {}", date);
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 날짜 형식입니다.");
        }
    }

    public String buildRankingKey(LocalDate date) {
        return RANKING_PRODUCT_CACHE_KEY_PREFIX + date.format(FORMATTER);
    }

    public String buildMemberKey(Long productId) {
        return RANKING_PRODUCT_CACHE_MEMBER_KEY + productId;
    }

    public LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("Error parsing date: {}", date);
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 날짜 형식입니다.");
        }
    }

    public void warmUpTomorrow(String tomorrowKey, List<RankingRaw> raws) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection redisConnection = (StringRedisConnection) connection;
            for (RankingRaw raw : raws) {
                if (raw.productId() == null) continue;
                String member = buildMemberKey(raw.productId());
                double score = raw.score() * ratio;
                redisConnection.zAdd(tomorrowKey, score, member);
            }
            return null;
        });

        // TTL 설정
        redisTemplate.expire(tomorrowKey, RANKING_CACHE_TTL);
    }
}
