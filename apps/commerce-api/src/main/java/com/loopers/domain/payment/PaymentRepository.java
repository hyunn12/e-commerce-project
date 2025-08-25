package com.loopers.domain.payment;

import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface PaymentRepository {

    Payment save(Payment payment);

    Payment getDetail(Long id);

    Payment getDetailByKey(String transactionKey);

    Payment getDetailByKeyWithLock(String transactionKey);

    List<Payment> getListPendingPayments(PaymentMethod method, PaymentStatus status, ZonedDateTime createdAt);
}
