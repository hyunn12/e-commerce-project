package com.loopers.interfaces.event.listener;

import com.loopers.application.order.CouponUseService;
import com.loopers.domain.event.dto.CouponUseEvent;
import com.loopers.kafka.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventHandler {

    private final CouponUseService couponUseService;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${kafka.topics.user}")
    private String userTopic;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleCouponUsed(CouponUseEvent event) {
        log.info("Coupon Use Event Handling: userCouponId={}, userId={}", event.getUserCouponId(), event.getUserId());
        couponUseService.use(event.getUserCouponId(), event.getUserId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishCouponUseEvent(CouponUseEvent event) {
        KafkaMessage<CouponUseEvent> message = KafkaMessage.of(event, "COUPON_USE");
        kafkaTemplate.send(userTopic, event.getUserId(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", userTopic, message);
    }
}
