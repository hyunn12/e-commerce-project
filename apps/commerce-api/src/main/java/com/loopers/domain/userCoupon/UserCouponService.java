package com.loopers.domain.userCoupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.loopers.support.utils.Validation.Message.*;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    @Transactional
    public UserCoupon issue(Long couponId, Long userId) {
        UserCoupon userCoupon = UserCoupon.create(couponId, userId);
        return userCouponRepository.save(userCoupon);
    }

    public UserCoupon getDetail(Long userCouponId, Long userId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId);
        if (userCoupon == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_COUPON_NOT_FOUND);
        }

        if (!userCoupon.isUsable()) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_COUPON_UNUSABLE);
        }

        if (!userCoupon.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_COUPON_INVALID_USER);
        }

        return userCoupon;
    }

    public void markUsed(UserCoupon userCoupon) {
        userCoupon.use();
    }

    public CouponHistory saveHistory(CouponHistory couponHistory) {
        return userCouponRepository.saveHistory(couponHistory);
    }
}
