package com.loopers.domain.coupon;

import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository {

    Coupon findById(Long couponId);
}
