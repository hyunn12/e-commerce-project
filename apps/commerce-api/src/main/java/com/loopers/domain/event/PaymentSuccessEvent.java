package com.loopers.domain.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentSuccessEvent {

    private Long orderId;

    public static PaymentSuccessEvent of(Long orderId) {
        return new PaymentSuccessEvent(orderId);
    }
}
