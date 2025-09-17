package com.loopers.batch.scheduler;

import com.loopers.application.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyRankingScheduler {

    private final RankingService rankingService;

    @Scheduled(cron = "0 0 1 1 * ?") // 매 월 1일 오전 1시
    public void runMonthlyRankingJob() {
        rankingService.runMonthlyJob();
    }
}
