package com.loopers.application.payment;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.domain.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRetryService {

    private final OrderService orderService;
    private final PaymentRestoreService paymentRestoreService;
    private final PaymentGateway paymentGateway;

    @Transactional
    public void processPaymentStatus(Payment payment) {
        try {
            Order order = orderService.getDetail(payment.getOrderId());
            orderService.checkWaitingOrder(order);

            PaymentResponse response = paymentGateway.getTransaction(payment.getTransactionKey());

            switch (response.getStatus()) {
                case SUCCESS:
                    payment.setPaymentSuccess(response.getReason());
                    order.markPaid();
                    log.info("결제 성공: orderId={} paymentId={}", order.getId(), payment.getId());
                    break;
                case FAIL:
                    payment.setPaymentFailed(response.getReason());
                    order.markPaymentFailed();
                    paymentRestoreService.restore(order);
                    log.info("결제 실패: orderId={} paymentId={}", order.getId(), payment.getId());
                    break;
                case PENDING:
                    if (payment.getCreatedAt().isBefore(ZonedDateTime.now().minusMinutes(30))) {
                        payment.setPaymentFailed("결제가 지연되어 취소되었습니다.");
                        order.markPaymentFailed();
                        paymentRestoreService.restore(order);
                        log.info("결제 지연 취소: orderId={} paymentId={}", order.getId(), payment.getId());
                        return;
                    }
                    log.info("결제 대기중: orderId={} paymentId={}", order.getId(), payment.getId());
                    break;
            }
        } catch (Exception e) {
            log.error("결제 처리 중 예외 발생: paymentId={} error={}", payment.getId(), e.getLocalizedMessage());
        }
    }
}
