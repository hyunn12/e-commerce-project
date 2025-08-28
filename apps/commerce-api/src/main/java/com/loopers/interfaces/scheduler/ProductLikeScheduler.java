package com.loopers.interfaces.scheduler;

import com.loopers.application.product.ProductFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductLikeScheduler {

    private final ProductFacade productFacade;

    @Scheduled(cron = "0 0 0 * * *") // 매일 0시
    public void refreshLikeCounts() {
        productFacade.refreshLikeCounts();
    }
}
