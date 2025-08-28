package com.loopers.interfaces.event.listener;

import com.loopers.application.order.CouponUseService;
import com.loopers.domain.event.dto.CouponUseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventHandler {

    private final CouponUseService couponUseService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(CouponUseEvent event) {
        log.info("Coupon Use Event Handling: userCouponId={}, userId={}", event.getUserCouponId(), event.getUserId());
        couponUseService.use(event.getUserCouponId(), event.getUserId());
    }
}
