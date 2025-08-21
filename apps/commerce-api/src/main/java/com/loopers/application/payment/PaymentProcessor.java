package com.loopers.application.payment;

import com.loopers.domain.payment.dto.PaymentRequest;
import com.loopers.domain.payment.dto.PaymentResponse;

public interface PaymentProcessor {
    PaymentResponse process(PaymentRequest request);
}
