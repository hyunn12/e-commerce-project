package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionKey(String transactionKey);

    List<Payment> findByMethodAndStatusAndCreatedAtBefore(PaymentMethod method, PaymentStatus status, ZonedDateTime createdAt);
}
