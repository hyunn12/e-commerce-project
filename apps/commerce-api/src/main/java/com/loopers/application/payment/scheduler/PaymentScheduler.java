package com.loopers.application.payment.scheduler;

import com.loopers.application.payment.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {

    private final PaymentFacade paymentFacade;

    @Scheduled(fixedDelay = 600000) // 10분
    public void retryPendingPayments() {
        paymentFacade.retryPendingPayments();
    }
}
