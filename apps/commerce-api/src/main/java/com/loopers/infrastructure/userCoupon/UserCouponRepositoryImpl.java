package com.loopers.infrastructure.userCoupon;

import com.loopers.domain.userCoupon.CouponHistory;
import com.loopers.domain.userCoupon.UserCoupon;
import com.loopers.domain.userCoupon.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final UserCouponJpaRepository userCouponJpaRepository;
    private final CouponHistoryJpaRepository couponHistoryJpaRepository;

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public UserCoupon findById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId).orElse(null);
    }

    @Override
    public CouponHistory saveHistory(CouponHistory couponHistory) {
        return couponHistoryJpaRepository.save(couponHistory);
    }
}
