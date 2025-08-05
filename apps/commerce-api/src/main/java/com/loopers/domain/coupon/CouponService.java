package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.loopers.support.utils.Validation.Message.MESSAGE_COUPON_MIN_AMOUNT;

@Service
@RequiredArgsConstructor
public class CouponService {

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
