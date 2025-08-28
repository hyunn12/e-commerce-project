package com.loopers.domain.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeDeleteEvent {

    private Long productId;

    public static LikeDeleteEvent of(Long productId) {
        return new LikeDeleteEvent(productId);
    }
}
