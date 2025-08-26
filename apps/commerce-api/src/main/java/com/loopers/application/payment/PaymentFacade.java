package com.loopers.application.payment;

import com.loopers.application.order.ExternalOrderSender;
import com.loopers.application.payment.dto.PaymentCommand;
import com.loopers.application.payment.dto.PaymentInfo;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.dto.PaymentResponse;
import com.loopers.domain.payment.dto.PaymentResponseResult;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentGatewayService paymentGatewayService;
    private final PaymentRestoreService paymentRestoreService;
    private final PaymentRetryService paymentRetryService;
    private final ExternalOrderSender externalOrderSender;
    private final PaymentAlertSender paymentAlertSender;
    private final OrderService orderService;
    private final PaymentService paymentService;

    @Transactional
    public PaymentInfo.Main payment(PaymentCommand.Create command) {
        // 주문 상태 조회
        Order order = orderService.getDetailWithLock(command.getOrderId());
        orderService.checkInitOrder(order, command.getUserId());
        order.markWaitingPayment();

        // 결제 생성
        Payment payment = paymentService.create(command.getUserId(), order.getId(), order.getPaymentAmount(), command.getMethod());

        // PG 결제 요청
        PaymentResponse response = paymentGatewayService.requestPayment(command.toRequest(order, payment));
        if (response.getStatus() == PaymentResponseResult.SUCCESS) {
            payment.setPaymentPending(response.getTransactionKey());
        }

        return PaymentInfo.Main.from(payment);
    }

    @Transactional
    public void paymentCallback(PaymentCommand.Callback command) {
        try {
            // PG 결제 조회
            PaymentInfo.Callback info = paymentGatewayService.getTransaction(command.getTransactionKey());
            if (info.getResult() == PaymentResponseResult.FAIL) {
                throw new CoreException(ErrorType.BAD_REQUEST, "결제 결과가 일치하지 않습니다.");
            }

            Payment payment = paymentService.getDetailByKeyWithLock(command.getTransactionKey());
            paymentService.checkPendingPayment(payment);

            Order order = orderService.getDetailWithLock(payment.getOrderId());
            orderService.checkWaitingOrder(order);

            switch (command.getResult()) {
                case SUCCESS -> {
                    payment.setPaymentSuccess(command.getReason());
                    order.markPaid();
                    externalOrderSender.send(order);
                }
                case FAIL -> {
                    payment.setPaymentFailed(command.getReason());
                    order.markPaymentFailed();
                    paymentRestoreService.restore(order);
                }
            }
        } catch (Exception e) {
            log.error("PG 콜백 처리 실패: command={}", command, e);

            // 알림 전송
            paymentAlertSender.sendFail(Map.of("transactionKey", command.getTransactionKey()), e);
        }
    }

    public void retryPendingPayments() {
        List<Payment> pendingPayments = paymentService.getListPendingPayments();
        for (Payment payment : pendingPayments) {
            paymentRetryService.processPaymentStatus(payment);
        }
    }
}
