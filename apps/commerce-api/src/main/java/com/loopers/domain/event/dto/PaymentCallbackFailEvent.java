package com.loopers.domain.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCallbackFailEvent {

    private Map<String, Object> params;
    private String message;

    public static PaymentCallbackFailEvent of(Map<String, Object> params, String message) {
        return new PaymentCallbackFailEvent(params, message);
    }
}
