package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment save(Long userId, int paymentAmount) {
        Payment payment = Payment.createBuilder()
                .userId(userId)
                .paymentAmount(paymentAmount)
                .build();
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment markFail(Payment payment) {
        payment.markFail();
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment markCancel(Payment payment) {
        payment.markCancel();
        return paymentRepository.save(payment);
    }
}
