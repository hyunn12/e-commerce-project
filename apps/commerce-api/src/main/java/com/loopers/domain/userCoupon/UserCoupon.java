package com.loopers.domain.userCoupon;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

import static com.loopers.support.utils.Validation.Message.MESSAGE_COUPON_USED;

@Getter
@Entity
@Table(name = "user_coupon")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon extends BaseEntity {

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserCouponStatus status;

    @Column(name = "used_at")
    private ZonedDateTime usedAt;

    @Column(name = "expired_at")
    private ZonedDateTime expiredAt;

    private UserCoupon(Long couponId, Long userId) {
        this.couponId = couponId;
        this.userId = userId;
        this.status = UserCouponStatus.UNUSED;
    }

    public static UserCoupon create(Long couponId, Long userId) {
        return new UserCoupon(couponId, userId);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isExpired() {
        return expiredAt != null;
    }

    public boolean isUsable() {
        return status == UserCouponStatus.UNUSED && !isExpired();
    }

    public void use() {
        if (this.status != UserCouponStatus.UNUSED) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_COUPON_USED);
        }
        this.status = UserCouponStatus.USED;
        this.usedAt = ZonedDateTime.now();
    }
}
