package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.loopers.domain.coupon.DiscountType.PRICE;
import static com.loopers.domain.coupon.DiscountType.RATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceDomainTest {
    
    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Nested
    class 쿠폰_조회_시 {

        @DisplayName("존재하는 쿠폰 ID로 조회하면 쿠폰이 반환된다.")
        @Test
        void getDetail_withValidId_returnsCoupon() {
            // arrange
            Long couponId = 1L;
            Coupon expectedCoupon = mock(Coupon.class);
            when(couponRepository.findById(couponId)).thenReturn(expectedCoupon);

            // act
            Coupon actual = couponService.getDetail(couponId);

            // assert
            verify(couponRepository).findById(couponId);
            assertThat(actual).isEqualTo(expectedCoupon);
        }

        @DisplayName("존재하지 않는 쿠폰 ID로 조회하면 Not Found 예외를 던진다.")
        @Test
        void throwNotFound_withInvalidId() {
            // arrange
            Long couponId = 999L;
            when(couponRepository.findById(couponId)).thenReturn(null);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> couponService.getDetail(couponId));

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 주문_금액_검증_시 {

        @DisplayName("최소 주문 금액보다 작으면 Bad Request 예외가 발생한다.")
        @Test
        void throwBadRequest_whenOrderAmountIsUnderMinAmount() {
            // arrange
            int minAmount = 10000;
            Coupon coupon = new Coupon("3000원 할인", 100, 0, PRICE, 3000, null, minAmount);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> couponService.validateAmount(coupon, minAmount - 1000));

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 할인_금액_계산_시 {

        @Test
        @DisplayName("정액 쿠폰인 경우, 할인 금액은 주문 금액과 할인값 중 작은 값이다.")
        void returnDiscountAmount_whenPriceCoupon() {
            // arrange
            int discountValue = 3000;
            int orderAmount = 20000;
            Coupon coupon = new Coupon("3천원 할인", 100, 0, PRICE, discountValue, null, 10000);

            // act
            int discount = couponService.calculateDiscountAmount(coupon, orderAmount);

            // assert
            assertThat(orderAmount).isGreaterThan(discountValue);
            assertThat(discount).isEqualTo(discountValue);
        }

        @Test
        @DisplayName("정률 쿠폰일 경우, 할인 금액은 계산값과 최대 할인값 중 작은 값이다.")
        void returnDiscountAmount_whenRateCoupon() {
            // arrange
            int discountValue = 30;
            int maxDiscountAmount = 3000;
            int orderAmount = 20000;
            Coupon coupon = new Coupon("30% 할인", 100, 0, RATE, discountValue, maxDiscountAmount, 1000);
            int calculated = (int) (orderAmount * coupon.getDiscountValue() / 100.0);

            // act
            int discount = couponService.calculateDiscountAmount(coupon, orderAmount);

            // assert
            assertThat(calculated).isGreaterThan(maxDiscountAmount);
            assertThat(discount).isEqualTo(maxDiscountAmount);
        }
    }
}
