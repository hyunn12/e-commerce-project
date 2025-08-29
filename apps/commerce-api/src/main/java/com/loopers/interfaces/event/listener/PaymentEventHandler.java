package com.loopers.interfaces.event.listener;

import com.loopers.application.order.ExternalOrderSender;
import com.loopers.application.payment.PaymentAlertSender;
import com.loopers.application.payment.PaymentRestoreService;
import com.loopers.domain.event.dto.PaymentCallbackFailEvent;
import com.loopers.domain.event.dto.PaymentFailEvent;
import com.loopers.domain.event.dto.PaymentRequestSuccessEvent;
import com.loopers.domain.event.dto.PaymentSuccessEvent;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final OrderService orderService;
    private final ExternalOrderSender externalOrderSender;
    private final PaymentRestoreService paymentRestoreService;
    private final PaymentAlertSender paymentAlertSender;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handle(PaymentRequestSuccessEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        order.markWaitingPayment();
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handle(PaymentSuccessEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        order.markPaid();
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handle(PaymentFailEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        order.markPaymentFailed();
        paymentRestoreService.restore(order);
    }

    @Async
    @TransactionalEventListener
    public void handleExternalSend(PaymentSuccessEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        externalOrderSender.send(order);
    }

    @Async
    @TransactionalEventListener
    public void handleExternalSend(PaymentFailEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        externalOrderSender.send(order);
    }

    @Async
    @TransactionalEventListener
    public void handle(PaymentCallbackFailEvent event) {
        paymentAlertSender.sendFail(event.getParams(), event.getMessage());
    }
}
