package com.loopers.domain.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponUseEvent {

    private Long userCouponId;
    private Long userId;

    public static CouponUseEvent of(Long userCouponId, Long userId) {
        return new CouponUseEvent(userCouponId, userId);
    }
}
