package com.loopers.infrastructure.userCoupon;

import com.loopers.domain.userCoupon.CouponUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponHistoryJpaRepository extends JpaRepository<CouponUsageHistory, Long> {
}
