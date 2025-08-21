package com.loopers.application.payment;

import com.loopers.domain.payment.dto.PaymentRequest;

public interface PaymentProcessor {
    void process(PaymentRequest request);
}
