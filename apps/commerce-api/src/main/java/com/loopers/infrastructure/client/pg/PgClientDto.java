package com.loopers.infrastructure.client.pg;

import com.loopers.domain.payment.dto.PaymentRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PgClientDto {

    public record PgRequest(
            String orderId,
            String cardType,
            String cardNo,
            int paymentAmount,
            String callbackUrl
    ) {
        public PgRequest from(PaymentRequest request, String callbackUrl) {
            return new PgRequest(
                    request.getOrderNo(),
                    request.getCardType().toString(),
                    request.getCardNo(),
                    request.getPaymentAmount(),
                    callbackUrl
            );
        }
    }

    public record PgResponse(
            String transactionKey,
            String orderId,
            String status,
            String reason
    ) { }
}
