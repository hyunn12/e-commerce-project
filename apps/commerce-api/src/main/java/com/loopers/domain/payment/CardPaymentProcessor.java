package com.loopers.domain.payment;

import com.loopers.application.payment.PaymentGateway;
import com.loopers.application.payment.PaymentProcessor;
import com.loopers.domain.payment.dto.PaymentRequest;
import com.loopers.domain.payment.dto.PaymentResponse;
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
public class CardPaymentProcessor implements PaymentProcessor {

    private final PaymentGateway paymentGateway;
    private final PaymentService paymentService;

    @Value("${client.pg-simulator.callback-url.ver-1}")
    private String callbackUrl;

    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "fallback")
    @Retry(name = "pgRetry", fallbackMethod = "fallback")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public PaymentResponse process(PaymentRequest request) {
        Payment payment = paymentService.getDetail(request.getPaymentId());

        // 결제 API 호출
        PaymentResponse response = paymentGateway.requestPayment(request, callbackUrl);
        if (response.getStatus().equals("SUCCESS")) {
            payment.setPaymentPending(response.getTransactionKey());
        }

        return response;
    }

    public PaymentResponse fallback(PaymentRequest request, Throwable throwable) {
        return PaymentResponse.fail("결제에 실패했습니다." + throwable.getLocalizedMessage());
    }
}
