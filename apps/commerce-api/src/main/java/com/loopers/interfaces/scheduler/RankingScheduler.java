package com.loopers.interfaces.scheduler;

import com.loopers.application.ranking.RankingFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingScheduler {

    private final RankingFacade rankingFacade;

    @Scheduled(cron = "0 30 23 * * *") // 매일 23시 50분
    public void warmUpRanking() {
        rankingFacade.warmUpTomorrowRanking();
    }
}
