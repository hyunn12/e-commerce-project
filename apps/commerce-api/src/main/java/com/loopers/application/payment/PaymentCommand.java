package com.loopers.application.payment;

import com.loopers.domain.order.Order;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.dto.CardType;
import com.loopers.domain.payment.dto.PaymentRequest;
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
        private Long paymentId;
        private CardType cardType;
        private String cardNo;
        private int paymentAmount;
        private String callbackUrl;

        public PaymentRequest toRequest(Order order, Payment payment) {
            return PaymentRequest.create(
                    order.getOrderNo().toString(),
                    payment.getId(),
                    payment.getMethod(),
                    cardType,
                    cardNo,
                    paymentAmount,
                    userId
            );
        }
    }
}
