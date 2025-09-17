package com.loopers.domain.ranking;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.loopers.redis.config.CacheConstants.*;

public class RankingKeyGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(RANKING_DATE_PATTERN);

    public static String buildRankingKey(String date) {
        try {
            LocalDate parsed = LocalDate.parse(date, FORMATTER);
            return buildRankingKey(parsed);
        } catch (DateTimeParseException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 날짜 형식입니다.");
        }
    }

    public static String buildRankingKey(LocalDate date) {
        return RANKING_PRODUCT_CACHE_KEY_PREFIX + date.format(FORMATTER);
    }

    public static String buildMemberKey(Long productId) {
        return RANKING_PRODUCT_CACHE_MEMBER_KEY + productId;
    }

    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 날짜 형식입니다.");
        }
    }
}
