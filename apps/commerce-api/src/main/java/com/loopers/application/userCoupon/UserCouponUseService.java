package com.loopers.application.userCoupon;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.userCoupon.UserCoupon;
import com.loopers.domain.userCoupon.UserCouponService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.loopers.support.utils.Validation.Message.MESSAGE_COUPON_UNUSABLE;

@Service
@RequiredArgsConstructor
public class UserCouponUseService {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @Transactional
    public int use(Long userCouponId, Long userId, int orderAmount) {
        UserCoupon userCoupon = userCouponService.getDetail(userCouponId, userId);

        Coupon coupon = couponService.getDetail(userCoupon.getCouponId());
        couponService.validateAmount(coupon, orderAmount);
        int discountAmount = couponService.calculateDiscountAmount(coupon, orderAmount);

        try {
            userCouponService.use(userCouponId, userId);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CoreException(ErrorType.CONFLICT, MESSAGE_COUPON_UNUSABLE);
        }

        return discountAmount;
    }
}
