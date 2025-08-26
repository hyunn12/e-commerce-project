package com.loopers.application.payment;

import com.loopers.application.payment.dto.PaymentInfo;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.dto.PaymentRequest;
import com.loopers.domain.payment.dto.PaymentResponse;
import com.loopers.domain.payment.dto.PaymentResponseResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final PaymentGateway paymentGateway;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final PaymentRestoreService paymentRestoreService;

    @Value("${client.pg-simulator.callback-url.ver-1}")
    private String callbackUrl;

    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "fallback")
    @Retry(name = "pgRetry", fallbackMethod = "fallback")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentResponse requestPayment(PaymentRequest request) {
        return paymentGateway.requestPayment(request, callbackUrl);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentResponse fallback(PaymentRequest request, Throwable throwable) {
        Payment payment = paymentService.getDetail(request.getPaymentId());
        Order order = orderService.getDetail(payment.getOrderId());

        payment.setPaymentFailed("PG 결제 요청이 실패했습니다.");
        order.markPaymentFailed();
        paymentRestoreService.restore(order);

        log.error("결제 요청 실패 fallback: orderId={} paymentId={} message={}", order.getId(), payment.getId(), throwable.getLocalizedMessage());

        return PaymentResponse.fail("결제에 실패했습니다." + throwable.getLocalizedMessage());
    }

    public PaymentInfo.Callback getTransaction(String transactionKey) {
        PaymentResponse response = paymentGateway.getTransaction(transactionKey);
        if (response.getStatus().equals(PaymentResponseResult.FAIL)) {
            return PaymentInfo.Callback.from(PaymentResponseResult.FAIL, response.getReason());
        }
        return PaymentInfo.Callback.from(PaymentResponseResult.SUCCESS, null);
    }
}
