package com.loopers.application.order;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.userCoupon.CouponUsageHistory;
import com.loopers.domain.userCoupon.UserCoupon;
import com.loopers.domain.userCoupon.UserCouponStatus;
import com.loopers.infrastructure.coupon.CouponJpaRepository;
import com.loopers.infrastructure.userCoupon.CouponUsageHistoryJpaRepository;
import com.loopers.infrastructure.userCoupon.UserCouponJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.loopers.domain.coupon.DiscountType.PRICE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponUseServiceConcurrencyTest {
    // sut --
    @Autowired
    private CouponUseService couponUseService;

    // orm--
    @Autowired
    private UserCouponJpaRepository userCouponJpaRepository;
    @Autowired
    private CouponJpaRepository couponJpaRepository;
    @Autowired
    private CouponUsageHistoryJpaRepository couponUsageHistoryJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("낙관적 락 적용 시, 동시에 쿠폰을 사용해도 한 번만 사용된다.")
    @Test
    void useCouponWithOptimisticLock() throws InterruptedException {
        // arrange
        Coupon coupon = couponJpaRepository.save(new Coupon("3천원 할인", 100, 0, PRICE, 3000, null, 10000));
        UserCoupon userCoupon = userCouponJpaRepository.save(UserCoupon.create(coupon.getId(), 1L));

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // act
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    couponUseService.use(userCoupon.getId(), userCoupon.getUserId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("[FAIL]: "+e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // assert
        System.out.println("[SUCCESS COUNT]: "+successCount.get());
        System.out.println("[FAIL COUNT]: "+failCount.get());
        assertThat(successCount.get()).isEqualTo(1);

        UserCoupon result = userCouponJpaRepository.findById(userCoupon.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(UserCouponStatus.USED);

        List<CouponUsageHistory> histories = couponUsageHistoryJpaRepository.findHistoriesByUserCouponId(userCoupon.getId());
        assertThat(histories).hasSize(1);
    }
}
