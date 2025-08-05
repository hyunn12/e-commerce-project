package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.loopers.support.utils.Validation.Message.MESSAGE_COUPON_MIN_AMOUNT;
import static com.loopers.support.utils.Validation.Message.MESSAGE_COUPON_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public Coupon getDetail(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        if (coupon == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_COUPON_NOT_FOUND);
        }
        return coupon;
    }

    public void validateAmount(Coupon coupon, int orderAmount) {
        if (coupon.isUnderMinAmount(orderAmount)) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_COUPON_MIN_AMOUNT);
        }
    }

    public int calculateDiscountAmount(Coupon coupon, int orderAmount) {
        return switch (coupon.getType()) {
            case PRICE -> Math.min(coupon.getDiscountValue(), orderAmount);
            case RATE -> {
                int calculated = (int) (orderAmount * coupon.getDiscountValue() / 100.0);
                yield Math.min(calculated, coupon.getMaxDiscountAmount());
            }
        };
    }
}
