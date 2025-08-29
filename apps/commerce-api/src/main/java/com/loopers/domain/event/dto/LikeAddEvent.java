package com.loopers.domain.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeAddEvent {

    private Long productId;
    private Long userId;

    public static LikeAddEvent of(Long productId, Long userId) {
        return new LikeAddEvent(productId, userId);
    }
}
