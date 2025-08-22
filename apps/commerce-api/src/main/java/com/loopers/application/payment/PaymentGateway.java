package com.loopers.application.payment;

import com.loopers.domain.payment.dto.PaymentRequest;
import com.loopers.domain.payment.dto.PaymentResponse;

import java.util.List;

public interface PaymentGateway {

    PaymentResponse requestPayment(PaymentRequest request, String callbackUrl);

    PaymentResponse getTransaction(String transactionKey);

    List<PaymentResponse> getTransactionsByOrder(String orderId);

}
