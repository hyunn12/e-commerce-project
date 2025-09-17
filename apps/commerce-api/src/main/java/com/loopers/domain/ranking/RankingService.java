package com.loopers.domain.ranking;

import com.loopers.application.ranking.RankingRaw;
import com.loopers.infrastructure.ranking.RankingRedisRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRedisRepository redisRepository;
    private final RankingRepository rankingRepository;
    private final RankingDateValidator dateValidator;

    @Value("${ranking.ratio}")
    private double ratio;

    public List<RankingRaw> getDailyRankings(String date, Pageable pageable) {
        dateValidator.validateDailyDate(date);
        String key = RankingKeyGenerator.buildRankingKey(date);

        if (!redisRepository.existsRankingKey(key)) {
            log.info("No ranking data found for date: {}", date);
            return List.of();
        }

        return redisRepository.getTopRankings(key, pageable);
    }

    public long getTotalDailyRankingCount(String date) {
        dateValidator.validateDailyDate(date);
        String key = RankingKeyGenerator.buildRankingKey(date);
        return redisRepository.getTotalRankingCount(key);
    }

    public Long getProductRank(Long productId, String date) {
        dateValidator.validateDailyDate(date);
        String key = RankingKeyGenerator.buildRankingKey(date);

        if (!redisRepository.existsRankingKey(key)) {
            throw new CoreException(ErrorType.NOT_FOUND, "랭킹 데이터가 존재하지 않습니다. date=" + date);
        }

        return redisRepository.getProductRanking(productId, key);
    }

    public List<RankingRaw> getWeeklyRankings(String yearWeek, Pageable pageable) {
        dateValidator.validateWeeklyDate(yearWeek);
        try {
            List<ProductWeeklyRanking> entities = rankingRepository.findWeeklyRankings(yearWeek, pageable);
            return entities.stream()
                    .map(e -> new RankingRaw(e.getProductId(), e.getTotalScore().doubleValue()))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get weekly rankings for: {}", yearWeek, e);
            throw new CoreException(ErrorType.INTERNAL_ERROR, "주간 랭킹 조회 실패");
        }
    }

    public long getTotalWeeklyRankingCount(String yearWeek) {
        dateValidator.validateWeeklyDate(yearWeek);
        try {
            return rankingRepository.countWeeklyRankings(yearWeek);
        } catch (Exception e) {
            log.error("Failed to count weekly rankings for: {}", yearWeek, e);
            throw new CoreException(ErrorType.INTERNAL_ERROR, "주간 랭킹 개수 조회 실패");
        }
    }

    public List<RankingRaw> getMonthlyRankings(String yearMonth, Pageable pageable) {
        dateValidator.validateMonthlyDate(yearMonth);
        try {
            List<ProductMonthlyRanking> entities = rankingRepository.findMonthlyRankings(yearMonth, pageable);
            return entities.stream()
                    .map(e -> new RankingRaw(e.getProductId(), e.getTotalScore().doubleValue()))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get monthly rankings for: {}", yearMonth, e);
            throw new CoreException(ErrorType.INTERNAL_ERROR, "월간 랭킹 조회 실패");
        }
    }

    public long getTotalMonthlyRankingCount(String yearMonth) {
        dateValidator.validateMonthlyDate(yearMonth);
        try {
            return rankingRepository.countMonthlyRankings(yearMonth);
        } catch (Exception e) {
            log.error("Failed to count monthly rankings for: {}", yearMonth, e);
            throw new CoreException(ErrorType.INTERNAL_ERROR, "월간 랭킹 개수 조회 실패");
        }
    }

    public void warmUpTomorrow() {
        LocalDate today = LocalDate.now();
        String todayKey = RankingKeyGenerator.buildRankingKey(today);
        String tomorrowKey = RankingKeyGenerator.buildRankingKey(today.plusDays(1));

        List<RankingRaw> top100 = redisRepository.getTopRankings(todayKey, Pageable.ofSize(100));
        if (top100.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "오늘 랭킹 데이터 없음: " + today);
        }

        if (redisRepository.getTotalRankingCount(tomorrowKey) > 0) {
            return;
        }

        redisRepository.warmUpTomorrow(tomorrowKey, top100, ratio);
    }
}
