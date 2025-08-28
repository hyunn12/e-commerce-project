package com.loopers.interfaces.event.listener;

import com.loopers.application.order.ExternalOrderSender;
import com.loopers.application.payment.PaymentAlertSender;
import com.loopers.application.payment.PaymentRestoreService;
import com.loopers.domain.event.PaymentCallbackFailEvent;
import com.loopers.domain.event.PaymentFailEvent;
import com.loopers.domain.event.PaymentRequestSuccessEvent;
import com.loopers.domain.event.PaymentSuccessEvent;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final OrderService orderService;
    private final ExternalOrderSender externalOrderSender;
    private final PaymentRestoreService paymentRestoreService;
    private final PaymentAlertSender paymentAlertSender;

    @Async
    @TransactionalEventListener
    public void handle(PaymentRequestSuccessEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        order.markWaitingPayment();
    }

    @Async
    @TransactionalEventListener
    public void handle(PaymentSuccessEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        order.markPaid();
        externalOrderSender.send(order);
    }

    @Async
    @TransactionalEventListener
    public void handle(PaymentFailEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        order.markPaymentFailed();
        paymentRestoreService.restore(order);
        externalOrderSender.send(order);
    }

    @Async
    @TransactionalEventListener
    public void handle(PaymentCallbackFailEvent event) {
        paymentAlertSender.sendFail(event.getParams(), event.getMessage());
    }
}
