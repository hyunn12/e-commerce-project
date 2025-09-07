package com.loopers.interfaces.event.listener;

import com.loopers.domain.event.dto.PointHistoryEvent;
import com.loopers.domain.point.PointHistory;
import com.loopers.domain.point.PointService;
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
public class PointHistoryEventHandler {

    private final PointService pointService;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${kafka.topics.user}")
    private String userTopic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePointHistory(PointHistoryEvent event) {
        PointHistory history = PointHistory.of(event.getUserId(), event.getAmount(), event.getType(), event.getOrderId());
        pointService.saveHistory(history);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPointHistoryEvent(PointHistoryEvent event) {
        KafkaMessage<PointHistoryEvent> message = KafkaMessage.of(event, "POINT_HISTORY");
        kafkaTemplate.send(userTopic, event.getUserId(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", userTopic, message);
    }
}
