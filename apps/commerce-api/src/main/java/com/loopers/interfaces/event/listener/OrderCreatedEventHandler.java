package com.loopers.interfaces.event.listener;

import com.loopers.application.order.ExternalOrderSender;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.application.payment.dto.PaymentCommand;
import com.loopers.domain.event.dto.LikeDeleteEvent;
import com.loopers.domain.event.dto.OrderCreatedEvent;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.interfaces.event.dto.KafkaMessage;
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
public class OrderCreatedEventHandler {

    private final PaymentFacade paymentFacade;
    private final ExternalOrderSender externalOrderSender;
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order}")
    private String orderTopic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePayment(OrderCreatedEvent event) {
        PaymentCommand.Create command = PaymentCommand.Create.builder()
                .userId(event.getUserId())
                .orderId(event.getOrderId())
                .method(event.getMethod())
                .cardType(event.getCardType())
                .cardNo(event.getCardNo())
                .paymentAmount(event.getPaymentAmount())
                .build();
        paymentFacade.payment(command);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleExternalOrderSend(OrderCreatedEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        externalOrderSender.send(order);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        KafkaMessage<OrderCreatedEvent> message = KafkaMessage.of(event);
        kafkaTemplate.send(orderTopic, event.getOrderId().toString(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", orderTopic, message);
    }
}
