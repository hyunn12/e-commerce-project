package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.dto.PaymentCommand;
import com.loopers.application.payment.dto.PaymentInfo;
import com.loopers.domain.payment.dto.CardType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentV1Dto {

    public static class PaymentRequest {

        public record Create(
                Long orderId,
                CardType cardType,
                String cardNo,
                int paymentAmount
        ) {
            public PaymentCommand.Create toCommand(Long userId) {
                return PaymentCommand.Create.builder()
                        .userId(userId)
                        .orderId(orderId)
                        .cardType(cardType)
                        .cardNo(cardNo)
                        .paymentAmount(paymentAmount)
                        .build();
            }
        }

        public record Modify(
                String orderNo,
                String transactionKey,
                String status,
                String reason
        ) {
            public PaymentCommand.Modify toCommand() {
                return PaymentCommand.Modify.builder()
                        .orderNo(orderNo)
                        .transactionKey(transactionKey)
                        .status(status)
                        .reason(reason)
                        .build();
            }
        }
    }

    public static class PaymentResponse {

        public record Main(
                Long id,
                Long orderId,
                int paymentAmount,
                String status,
                String transactionKey
        ) {
            public static PaymentResponse.Main from(PaymentInfo.Main info) {
                return new PaymentResponse.Main(
                        info.getId(),
                        info.getOrderId(),
                        info.getPaymentAmount(),
                        info.getStatus().toString(),
                        info.getTransactionKey()
                );
            }
        }
    }
}
