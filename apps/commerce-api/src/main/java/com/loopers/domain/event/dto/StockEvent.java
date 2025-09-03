package com.loopers.domain.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockEvent {

    private Long productId;
    private int quantity;

    public static StockEvent of(Long productId, int quantity) {
        return new StockEvent(productId, quantity);
    }
}
