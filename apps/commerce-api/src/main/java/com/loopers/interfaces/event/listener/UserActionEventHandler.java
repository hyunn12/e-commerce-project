package com.loopers.interfaces.event.listener;

import com.loopers.application.userAction.ExternalUserActionSender;
import com.loopers.domain.event.dto.*;
import com.loopers.interfaces.event.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.loopers.domain.event.dto.UserAction.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionEventHandler {

    private final ExternalUserActionSender externalUserActionSender;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${kafka.topics.catalog}")
    private String catalogTopic;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeAdded(LikeAddEvent event) {
        externalUserActionSender.send(UserActionEvent.of(event.getUserId(), LIKE_ADD, event.getProductId()));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeDeleted(LikeDeleteEvent event) {
        externalUserActionSender.send(UserActionEvent.of(event.getUserId(), LIKE_DELETE, event.getProductId()));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedEvent event) {
        externalUserActionSender.send(UserActionEvent.of(event.getUserId(), ORDER, event.getOrderId()));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductViewed(ProductViewEvent event) {
        externalUserActionSender.send(UserActionEvent.of(event.getUserId(), VIEW, event.getProductId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processProductViewEvent(ProductViewEvent event) {
        KafkaMessage<ProductViewEvent> message = KafkaMessage.of(event, "PRODUCT_VIEW");
        kafkaTemplate.send(catalogTopic, event.getUserId(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", catalogTopic, message);
    }
}
