package com.loopers.application.payment;

import com.loopers.domain.payment.dto.PaymentRequest;
import com.loopers.domain.payment.dto.PaymentResponse;

public interface PaymentGateway {

    PaymentResponse requestPayment(PaymentRequest request, String callbackUrl);

    PaymentResponse getTransaction(String transactionKey);

}
