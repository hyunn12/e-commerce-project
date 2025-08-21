package com.loopers.domain.payment;

import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {

    Payment save(Payment payment);

    Payment getDetail(Long id);

    Payment getDetailByKey(String transactionKey);
}
