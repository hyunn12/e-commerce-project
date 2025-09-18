package com.loopers.batch.scheduler;

import com.loopers.application.RankingJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyRankingScheduler {

    private final RankingJobService rankingJobService;

    @Scheduled(cron = "0 0 1 * * *") // 매일 오전 1시
    public void runMonthlyRankingJob() {
        rankingJobService.runMonthlyJob();
    }
}
