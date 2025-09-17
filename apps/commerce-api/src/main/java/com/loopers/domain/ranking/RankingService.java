package com.loopers.domain.ranking;

import com.loopers.application.ranking.RankingRaw;
import com.loopers.infrastructure.ranking.RankingRedisRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRedisRepository redisRepository;

    @Value("${ranking.ratio}")
    private double ratio;

    public List<RankingRaw> getDailyRankings(String date, Pageable pageable) {
        String key = RankingKeyGenerator.buildRankingKey(date);

        if (!redisRepository.existsRankingKey(key)) {
            throw new CoreException(ErrorType.NOT_FOUND, "랭킹 데이터가 존재하지 않습니다. date=" + date);
        }

        return redisRepository.getTopRankings(key, pageable);
    }

    public long getTotalRankingCount(String date) {
        String key = RankingKeyGenerator.buildRankingKey(date);
        return redisRepository.getTotalRankingCount(key);
    }

    public Long getProductRank(Long productId, String date) {
        String key = RankingKeyGenerator.buildRankingKey(date);

        if (!redisRepository.existsRankingKey(key)) {
            throw new CoreException(ErrorType.NOT_FOUND, "랭킹 데이터가 존재하지 않습니다. date=" + date);
        }

        return redisRepository.getProductRanking(productId, key);
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
