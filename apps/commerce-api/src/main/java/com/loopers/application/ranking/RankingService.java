package com.loopers.application.ranking;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
        String member = RANKING_PRODUCT_CACHE_MEMBER_KEY + productId;
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
}
