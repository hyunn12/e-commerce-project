package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment create(Long userId, Long orderId, int paymentAmount, PaymentMethod method) {
        Payment payment = Payment.createBuilder()
                .userId(userId)
                .orderId(orderId)
                .paymentAmount(paymentAmount)
                .method(method)
                .build();
        return paymentRepository.save(payment);
    }

    public Payment getDetail(Long paymentId) {
        Payment payment = paymentRepository.getDetail(paymentId);
        if (payment == null) {
            throw new IllegalStateException("결제 정보를 찾을 수 없습니다.");
        }
        return payment;
    }

    public Payment getDetailByKey(String transactionKey) {
        return paymentRepository.getDetailByKey(transactionKey);
    }
}
