package com.loopers.infrastructure.userCoupon;

import com.loopers.domain.userCoupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
}
