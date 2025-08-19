package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment create(Long userId, int paymentAmount) {
        Payment payment = Payment.createBuilder()
                .userId(userId)
                .paymentAmount(paymentAmount)
                .build();
        return paymentRepository.save(payment);
    }

    @Transactional
    public void markStatus(Payment payment, PaymentStatus status) {
        switch (status) {
            case WAITING -> payment.markWaiting();
            case SUCCESS -> payment.markSuccess();
            case CANCEL -> payment.markCancel();
            case FAIL -> payment.markFail();
        }
    }
}
