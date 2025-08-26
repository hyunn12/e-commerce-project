package com.loopers.application.payment.dto;

import com.loopers.domain.order.Order;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.payment.dto.CardType;
import com.loopers.domain.payment.dto.PaymentRequest;
import com.loopers.domain.payment.dto.PaymentResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {

    @Getter
    @Builder
    public static class Create {
        private Long userId;
        private Long orderId;
        private PaymentMethod method;
        private CardType cardType;
        private String cardNo;
        private int paymentAmount;

        public PaymentRequest toRequest(Order order, Payment payment) {
            return PaymentRequest.create(
                    order.getOrderNo().toString(),
                    payment.getId(),
                    method,
                    cardType,
                    cardNo,
                    paymentAmount,
                    userId
            );
        }
    }

    @Getter
    @Builder
    public static class Callback {
        private String orderNo;
        private String transactionKey;
        private PaymentResult result;
        private String reason;
    }
}
