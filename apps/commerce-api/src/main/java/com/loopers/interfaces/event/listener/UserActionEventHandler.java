package com.loopers.interfaces.event.listener;

import com.loopers.application.userAction.ExternalUserActionSender;
import com.loopers.domain.event.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.loopers.domain.event.dto.UserAction.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionEventHandler {

    private final ExternalUserActionSender externalUserActionSender;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeAdded(LikeAddEvent event) {
        externalUserActionSender.send(UserActionEvent.of(event.getUserId(), LIKE_ADD, event.getProductId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeDeleted(LikeDeleteEvent event) {
        externalUserActionSender.send(UserActionEvent.of(event.getUserId(), LIKE_DELETE, event.getProductId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedEvent event) {
        externalUserActionSender.send(UserActionEvent.of(event.getUserId(), ORDER, event.getOrderId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductViewed(ProductViewEvent event) {
        externalUserActionSender.send(UserActionEvent.of(event.getUserId(), VIEW, event.getProductId()));
    }
}
