package com.loopers.interfaces.event.listener;

import com.loopers.application.order.ExternalOrderSender;
import com.loopers.application.payment.PaymentAlertSender;
import com.loopers.application.payment.PaymentRestoreService;
import com.loopers.domain.event.dto.*;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final OrderService orderService;
    private final ExternalOrderSender externalOrderSender;
    private final PaymentRestoreService paymentRestoreService;
    private final PaymentAlertSender paymentAlertSender;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order}")
    private String orderTopic;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderWaiting(PaymentRequestSuccessEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        order.markWaitingPayment();
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPaid(PaymentSuccessEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        order.markPaid();
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentFailed(PaymentFailEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        order.markPaymentFailed();
        paymentRestoreService.restore(order);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleExternalSend(PaymentSuccessEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        externalOrderSender.send(order);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleExternalOrderSend(PaymentFailEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        externalOrderSender.send(order);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentAlertSend(PaymentCallbackFailEvent event) {
        paymentAlertSender.sendFail(event.getParams(), event.getMessage());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPaymentRequestSuccessEvent(PaymentRequestSuccessEvent event) {
        KafkaMessage<PaymentRequestSuccessEvent> message = KafkaMessage.of(event, "PAYMENT_REQUEST_SUCCESS");
        kafkaTemplate.send(orderTopic, event.getOrderId().toString(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", orderTopic, message);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPaymentSuccessEvent(PaymentSuccessEvent event) {
        KafkaMessage<PaymentSuccessEvent> message = KafkaMessage.of(event, "PAYMENT_SUCCESS");
        kafkaTemplate.send(orderTopic, event.getOrderId().toString(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", orderTopic, message);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPaymentFailEvent(PaymentFailEvent event) {
        KafkaMessage<PaymentFailEvent> message = KafkaMessage.of(event, "PAYMENT_FAIL");
        kafkaTemplate.send(orderTopic, event.getOrderId().toString(), message);
        log.info("Published KafkaMessage: topic: {}, message={}", orderTopic, message);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPaymentCallbackFailEvent(PaymentCallbackFailEvent event) {
        KafkaMessage<PaymentCallbackFailEvent> message = KafkaMessage.of(event, "PAYMENT_CALLBACK_FAIL");
        kafkaTemplate.send(orderTopic, (String) event.getParams().get("transactionKey"), message);
        log.info("Published KafkaMessage: topic: {}, message={}", orderTopic, message);
    }
}
