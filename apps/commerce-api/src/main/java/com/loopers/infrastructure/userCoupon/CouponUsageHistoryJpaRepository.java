package com.loopers.infrastructure.userCoupon;

import com.loopers.domain.userCoupon.CouponUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageHistoryJpaRepository extends JpaRepository<CouponUsageHistory, Long> {

    List<CouponUsageHistory> findHistoriesByUserCouponId(Long userCouponId);
}
