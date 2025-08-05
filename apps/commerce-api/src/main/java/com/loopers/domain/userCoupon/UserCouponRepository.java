package com.loopers.domain.userCoupon;

import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);

    UserCoupon findById(Long userCouponId);

    CouponHistory saveHistory(CouponHistory couponHistory);
}
