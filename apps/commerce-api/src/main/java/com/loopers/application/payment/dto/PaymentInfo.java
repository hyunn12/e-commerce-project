package com.loopers.application.payment.dto;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.domain.payment.dto.PaymentResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentInfo {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Main {
        private Long id;
        private Long orderId;
        private int paymentAmount;
        private PaymentStatus status;
        private String transactionKey;

        public static PaymentInfo.Main from(Payment payment) {
            return new PaymentInfo.Main(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getPaymentAmount(),
                    payment.getStatus(),
                    payment.getTransactionKey()
            );
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Callback {
        private PaymentResult result;
        private String message;

        public static PaymentInfo.Callback from(PaymentResult result, String message) {
            return new PaymentInfo.Callback(result, message);
        }
    }
}
