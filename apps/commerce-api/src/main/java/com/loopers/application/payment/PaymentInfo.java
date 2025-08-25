package com.loopers.application.payment;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentStatus;
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
        private String result;
        private String message;

        public static Callback from(String result, String message) {
            return new Callback(result, message);
        }
    }
}
