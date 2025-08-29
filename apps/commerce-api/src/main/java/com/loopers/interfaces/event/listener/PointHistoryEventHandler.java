package com.loopers.interfaces.event.listener;

import com.loopers.domain.event.dto.PointHistoryEvent;
import com.loopers.domain.point.PointHistory;
import com.loopers.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointHistoryEventHandler {

    private final PointService pointService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PointHistoryEvent event) {
        PointHistory history = PointHistory.of(event.getUserId(), event.getAmount(), event.getType(), event.getOrderId());
        pointService.saveHistory(history);
    }
}
