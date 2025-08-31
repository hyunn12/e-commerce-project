package com.loopers.domain.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentFailEvent {

    private Long orderId;

    public static PaymentFailEvent of(Long orderId) {
        return new PaymentFailEvent(orderId);
    }
}
