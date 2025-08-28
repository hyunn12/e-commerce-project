package com.loopers.interfaces.event.listener;

import com.loopers.application.order.ExternalOrderSender;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.application.payment.dto.PaymentCommand;
import com.loopers.domain.event.dto.OrderCreatedEvent;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventHandler {

    private final PaymentFacade paymentFacade;
    private final ExternalOrderSender externalOrderSender;
    private final OrderService orderService;

    @Async
    @TransactionalEventListener
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
    @TransactionalEventListener
    public void handleExternalSend(OrderCreatedEvent event) {
        Order order = orderService.getDetail(event.getOrderId());
        externalOrderSender.send(order);
    }
}
