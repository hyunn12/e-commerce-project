package com.loopers.domain.userCoupon;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupon_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponUsageHistory extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_coupon_id", nullable = false)
    private Long userCouponId;

    private CouponUsageHistory(Long userId, Long userCouponId) {
        this.userId = userId;
        this.userCouponId = userCouponId;
    }

    public static CouponUsageHistory create(Long userId, Long userCouponId) {
        return new CouponUsageHistory(userId, userCouponId);
    }
}
