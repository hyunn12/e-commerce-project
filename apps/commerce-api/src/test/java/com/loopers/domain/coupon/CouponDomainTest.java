package com.loopers.domain.coupon;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class CouponDomainTest {

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 최소_주문_금액_검증_시 {

        @DisplayName("주문금액이 최소금액 미만이면 true 반환한다.")
        @Test
        void returnTrue_whenOrderAmountIsUnderMinAmount() {
            // arrange
            int minAmount = 10000;
            Coupon coupon = new Coupon("3000원 할인", 100, 0, DiscountType.PRICE, 3000, null, minAmount);

            // act
            boolean result = coupon.isUnderMinAmount(minAmount-1000);

            // assert
            assertThat(result).isTrue();
        }

        @DisplayName("주문금액이 최소금액과 같거나 크면 false 반환한다.")
        @Test
        void returnFalse_whenOrderAmountIsEqualOrOverMinAmount() {
            // arrange
            int minAmount = 10000;
            Coupon coupon = new Coupon("3000원 할인", 100, 0, DiscountType.PRICE, 3000, null, minAmount);

            // act
            boolean result1 = coupon.isUnderMinAmount(minAmount);
            boolean result2 = coupon.isUnderMinAmount(minAmount+5000);

            // assert
            assertThat(result1).isFalse();
            assertThat(result2).isFalse();
        }
    }
}
