package com.loopers.domain.userCoupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceDomainTest {

    @Mock
    private UserCouponRepository userCouponRepository;


    @InjectMocks
    private UserCouponService userCouponService;

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 쿠폰_발급_시 {

        @DisplayName("정상적인 값이 주어지면 쿠폰 발급에 성공한다.")
        @Test
        void successIssueUserCoupon() {
            // arrange
            Long couponId = 1L;
            Long userId = 1L;
            UserCoupon userCoupon = UserCoupon.create(couponId, userId);
            when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(userCoupon);

            // act
            UserCoupon result = userCouponService.issue(couponId, userId);

            // assert
            assertThat(result.getCouponId()).isEqualTo(couponId);
            assertThat(result.getUserId()).isEqualTo(userId);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 쿠폰_상세_조회_시 {

        @DisplayName("정상적인 값이 주어진다면 쿠폰 정보 조회에 성공한다.")
        @Test
        void returnCoupon_whenValid() {
            // arrange
            Long userCouponId = 1L;
            Long userId = 1L;
            UserCoupon userCoupon = mock(UserCoupon.class);
            when(userCoupon.isUsable()).thenReturn(true);
            when(userCoupon.getUserId()).thenReturn(userId);
            when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);

            // act
            UserCoupon result = userCouponService.getDetail(userCouponId, userId);

            // assert
            assertThat(result).isEqualTo(userCoupon);
        }
        
        @Nested
        class 예외가_발생한다 {

            @DisplayName("존재하지 않는 쿠폰이라면 Not Found 예외가 발생한다.")
            @Test
            void throwNotFound_whenUserCouponNotExist() {
                // arrange
                Long userCouponId = 1L;
                Long userId = 1L;
                when(userCouponRepository.findById(userCouponId)).thenReturn(null);

                // act
                CoreException exception = assertThrows(CoreException.class, () -> userCouponService.getDetail(userCouponId, userId));
                
                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            }

            @Test
            @DisplayName("사용 불가능한 쿠폰이라면 Bad Request 예외가 발생한다.")
            void throwBadRequest_whenCouponNotUsable() {
                // arrange
                Long userCouponId = 1L;
                Long userId = 1L;
                UserCoupon mockCoupon = mock(UserCoupon.class);
                when(mockCoupon.isUsable()).thenReturn(false);
                when(userCouponRepository.findById(userCouponId)).thenReturn(mockCoupon);

                // act
                CoreException exception = assertThrows(CoreException.class, () -> userCouponService.getDetail(userCouponId, userId));
                
                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }

            @Test
            @DisplayName("쿠폰 소유자가 아니라면 Bad Request 예외가 발생한다.")
            void throwBadRequest_whenUserIdMismatch() {
                // arrange
                Long userCouponId = 1L;
                Long userId = 1L;
                UserCoupon userCoupon = mock(UserCoupon.class);
                when(userCoupon.isUsable()).thenReturn(true);
                when(userCoupon.getUserId()).thenReturn(999L);
                when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);

                // act
                CoreException exception = assertThrows(CoreException.class, () -> userCouponService.getDetail(userCouponId, userId));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 쿠폰_사용_처리_시 {

        @Test
        @DisplayName("쿠폰의 use() 메서드를 호출한다면 USED 처리된다.")
        void markUsed_callUse() {
            // arrange
            Long couponId = 1L;
            Long userId = 1L;
            UserCoupon userCoupon = UserCoupon.create(couponId, userId);

            // act
            userCouponService.markUsed(userCoupon);

            // assert
            assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
            assertThat(userCoupon.getUsedAt()).isNotNull();
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 쿠폰_사용_이력_저장_시 {

        @Test
        @DisplayName("정상적인 값이 주어지면 저장된 결과가 반환된다.")
        void saveHistory_returnsSavedEntity() {
            // given
            CouponUsageHistory couponUsageHistory = CouponUsageHistory.create(1L, 1L);
            given(userCouponRepository.saveHistory(couponUsageHistory)).willReturn(couponUsageHistory);

            // when
            CouponUsageHistory result = userCouponService.saveHistory(couponUsageHistory);

            // then
            assertThat(result).isEqualTo(couponUsageHistory);
            verify(userCouponRepository, times(1)).saveHistory(couponUsageHistory);
        }
    }
}
