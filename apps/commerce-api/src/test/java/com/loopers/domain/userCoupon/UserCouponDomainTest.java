package com.loopers.domain.userCoupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserCouponDomainTest {


    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 사용_여부_검증_시 {

        @DisplayName("usedAt이 null이면 false 반환한다.")
        @Test
        void returnFalse_whenUsedAtIsNull() {
            // arrange
            UserCoupon userCoupon = UserCoupon.create(1L, 1L);

            // act
            boolean result = userCoupon.isUsed();

            // assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("usedAt이 존재하면 true 반환한다")
        void returnTrue_whenUsedAtIsNotNull() {
            // arrange
            UserCoupon userCoupon = UserCoupon.create(1L, 1L);
            ReflectionTestUtils.setField(userCoupon, "usedAt", ZonedDateTime.now());

            // act
            boolean result = userCoupon.isUsed();

            // assert
            assertThat(result).isTrue();
        }
    }


    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 만료_여부_검증_시 {

        @DisplayName("expiredAt이 null이면 false 반환한다.")
        @Test
        void returnFalse_whenExpiredAtIsNull() {
            // arrange
            UserCoupon userCoupon = UserCoupon.create(1L, 1L);

            // act
            boolean result = userCoupon.isExpired();

            // assert
            assertThat(result).isFalse();
        }

        @DisplayName("expiredAt이 존재하면 true 반환한다.")
        @Test
        void returnTrue_whenExpiredAtIsNotNull() {
            // arrange
            UserCoupon userCoupon = UserCoupon.create(1L, 1L);
            ReflectionTestUtils.setField(userCoupon, "expiredAt", ZonedDateTime.now());

            // act
            boolean result = userCoupon.isExpired();

            // assert
            assertThat(result).isTrue();
        }
    }


    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 사용_가능_여부_검증_시 {

        @DisplayName("status가 UNUSED이고 만료되지 않았다면 true 반환한다.")
        @Test
        void returnTrue_whenStatusIsUNUSEDAndNotExpired() {
            // arrange
            UserCoupon userCoupon = UserCoupon.create(1L, 1L);

            // act
            boolean result = userCoupon.isUsable();

            // assert
            assertThat(result).isTrue();
        }

        @DisplayName("status가 UNUSED가 아니고 만료되지 않았다면 false 반환한다.")
        @Test
        void returnFalse_whenStatusIsNotUNUSEDAndNotExpired() {
            // arrange
            UserCoupon userCoupon = UserCoupon.create(1L, 1L);
            ReflectionTestUtils.setField(userCoupon, "status", UserCouponStatus.USED);

            // act
            boolean result = userCoupon.isUsable();

            // assert
            assertThat(result).isFalse();
        }

        @DisplayName("status가 UNUSED이고 만료되었다면 false 반환한다.")
        @Test
        void returnFalse_whenStatusIsUNUSEDAndExpired() {
            // arrange
            UserCoupon userCoupon = UserCoupon.create(1L, 1L);
            ReflectionTestUtils.setField(userCoupon, "expiredAt", ZonedDateTime.now());

            // act
            boolean result = userCoupon.isUsable();

            // assert
            assertThat(result).isFalse();
        }

    }


    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 사용_요청_시 {

        @Test
        @DisplayName("사용 가능한 상태에서 use 호출 시 상태가 USED 변경되고 usedAt 설정된다.")
        void changeStatusAndUsedAt_whenUsed() {
            // arrange
            UserCoupon userCoupon = UserCoupon.create(1L, 1L);

            // act
            userCoupon.use();

            // assert
            assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
            assertThat(userCoupon.getUsedAt()).isNotNull();
        }

        @Test
        @DisplayName("status USED 면 use 호출 시 400 Bad Request 예외가 발생한다.")
        void throwException_whenAlreadyUsed() {
            // arrange
            UserCoupon userCoupon = UserCoupon.create(1L, 1L);
            ReflectionTestUtils.setField(userCoupon, "status", UserCouponStatus.USED);

            // act
            CoreException exception = assertThrows(CoreException.class, userCoupon::use);

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("expiredAt 존재하면 use 호출 시 400 Bad Request 예외가 발생한다.")
        void throwException_whenExpired() {
            // arrange
            UserCoupon userCoupon = UserCoupon.create(1L, 1L);
            ReflectionTestUtils.setField(userCoupon, "expiredAt", ZonedDateTime.now());

            // act
            CoreException exception = assertThrows(CoreException.class, userCoupon::use);

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
