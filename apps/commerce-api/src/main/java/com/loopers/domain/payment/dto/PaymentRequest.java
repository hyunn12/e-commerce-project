package com.loopers.domain.payment.dto;

import com.loopers.domain.payment.PaymentMethod;
import com.loopers.infrastructure.client.pg.PgClientDto;
import lombok.Getter;

@Getter
public class PaymentRequest {

    private Long paymentId;
    private final String orderNo;
    private final PaymentMethod paymentMethod;
    private final CardType cardType;
    private final String cardNo;
    private final int paymentAmount;
    private final Long userId;

    private PaymentRequest(String orderNo, Long paymentId, PaymentMethod paymentMethod, CardType cardType, String cardNo, int paymentAmount, Long userId) {
        this.orderNo = orderNo;
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.cardType = cardType;
        this.cardNo = cardNo;
        this.paymentAmount = paymentAmount;
        this.userId = userId;
    }

    public static PaymentRequest create(String orderNo, Long paymentId, PaymentMethod paymentMethod, CardType cardType, String cardNumber, int finalTotalPrice, Long userId) {
        return new PaymentRequest(orderNo, paymentId, paymentMethod, cardType, cardNumber, finalTotalPrice, userId);
    }

    public PgClientDto.PgRequest toPgRequest(String callbackUrl) {
        return new PgClientDto.PgRequest(
                orderNo,
                cardType.toString(),
                cardNo,
                paymentAmount,
                callbackUrl
        );
    }
}
