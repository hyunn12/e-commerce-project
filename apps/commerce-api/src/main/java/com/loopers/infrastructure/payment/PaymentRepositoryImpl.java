package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.domain.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

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

    @Override
    public Payment getDetailByKeyWithLock(String transactionKey) {
        return paymentJpaRepository.findByTransactionKeyWithLock(transactionKey).orElse(null);
    }

    @Override
    public List<Payment> getListPendingPayments(PaymentMethod method, PaymentStatus status, ZonedDateTime createdAt) {
        return paymentJpaRepository.findByMethodAndStatusAndCreatedAtBefore(method, status, createdAt);
    }
}
