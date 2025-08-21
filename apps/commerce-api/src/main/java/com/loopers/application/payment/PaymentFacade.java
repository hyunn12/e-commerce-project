package com.loopers.application.payment;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final PaymentProcessor paymentProcessor;
    private final PaymentRestoreService paymentRestoreService;

    @Transactional
    public PaymentInfo.Main payment(PaymentCommand.Create command) {
        // 주문 상태 조회
        Order order = orderService.getDetail(command.getOrderId());
        orderService.checkInitOrder(order, command.getUserId());
        order.markWaitingPayment();

        // 결제 생성
        Payment payment = paymentService.create(command.getUserId(), order.getId(), order.getPaymentAmount(), PaymentMethod.CARD);

        // PG 결제 요청
        paymentProcessor.process(command.toRequest(order, payment));

        Payment result = paymentService.getDetail(payment.getId());
        return PaymentInfo.Main.from(result);
    }

    public PaymentInfo.Callback paymentCallback(PaymentCommand.Modify command) {
        try {
            // TODO PG 결제 상태 조회 추가

            Payment payment = paymentService.getDetailByKey(command.getTransactionKey());
            paymentService.checkPendingPayment(payment);

            Order order = orderService.getDetail(payment.getOrderId());
            orderService.checkWaitingOrder(order);

            if (command.getStatus().equals("SUCCESS")) {
                payment.setPaymentSuccess(command.getReason());
                order.markPaid();
            } else {
                payment.setPaymentFailed(command.getReason());
                order.markPaymentFailed();
                paymentRestoreService.restore(order);
            }

            return PaymentInfo.Callback.from("SUCCESS", null);
        } catch (Exception e) {
            return PaymentInfo.Callback.from("FAIL", e.getLocalizedMessage());
        }
    }
}
