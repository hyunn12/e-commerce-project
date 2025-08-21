package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment getDetail(Long id) {
        return paymentJpaRepository.findById(id).orElse(null);
    }

    @Override
    public Payment getDetailByKey(String transactionKey) {
        return paymentJpaRepository.findByTransactionKey(transactionKey).orElse(null);
    }
}
