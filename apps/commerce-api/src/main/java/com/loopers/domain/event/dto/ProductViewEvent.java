package com.loopers.domain.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductViewEvent {

    private Long productId;
    private Long userId;

    public static ProductViewEvent of(Long productId, Long userId) {
        return new ProductViewEvent(productId, userId);
    }
}
