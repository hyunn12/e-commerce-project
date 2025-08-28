package com.loopers.domain.event.dto;

import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.payment.dto.CardType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreatedEvent {

    private Long orderId;
    private Long userId;
    private int paymentAmount;
    private PaymentMethod method;
    private CardType cardType;
    private String cardNo;

    public static OrderCreatedEvent of(Long orderId, Long userId, int paymentAmount, PaymentMethod method, CardType cardType, String cardNo) {
        return new OrderCreatedEvent(orderId, userId, paymentAmount, method, cardType, cardNo);
    }
}
