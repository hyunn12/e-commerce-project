package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointDomainTest {

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 포인트_충전_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 포인트_금액_검증에_성공한다 {

            @DisplayName("유효한 포인트라면")
            @Test
            void validPoint() {
                // arrange
                int current = 10000;
                int amount = 30000;

                // act
                Point point = new Point(1L, current);
                point.add(amount);

                // assert
                assertThat(point.getPoint()).isEqualTo(current+amount);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 포인트_검증에_실패한_후_400_Bad_Request_예외가_발생한다 {

            @DisplayName("포인트가 음수라면")
            @Test
            void 포인트가_음수라면() {
                // arrange
                int current = 10000;
                int amount = -10000;

                // act
                Point point = new Point(1L, current);

                CoreException exception = assertThrows(CoreException.class, () -> point.add(amount));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }

            @DisplayName("포인트가 0이라면")
            @Test
            void 포인트가_0이라면() {
                // arrange
                int current = 10000;
                int amount = 0;

                // act
                Point point = new Point(1L, current);

                CoreException exception = assertThrows(CoreException.class, () -> point.add(amount));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 포인트_사용_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 포인트_금액_검증에_성공한다 {

            @DisplayName("유효한 포인트라면")
            @Test
            void validPoint() {
                // arrange
                int current = 30000;
                int amount = 10000;

                // act
                Point point = new Point(1L, current);
                point.use(amount);

                // assert
                assertThat(point.getPoint()).isEqualTo(current-amount);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 포인트_검증에_실패한_후_400_Bad_Request_예외가_발생한다 {

            @DisplayName("포인트가 음수라면")
            @Test
            void 포인트가_음수라면() {
                // arrange
                int current = 30000;
                int amount = -10000;

                // act
                Point point = new Point(1L, current);

                CoreException exception = assertThrows(CoreException.class, () -> point.use(amount));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }

            @DisplayName("포인트가 0이라면")
            @Test
            void 포인트가_0이라면() {
                // arrange
                int current = 30000;
                int amount = 0;

                // act
                Point point = new Point(1L, current);

                CoreException exception = assertThrows(CoreException.class, () -> point.use(amount));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }
        }

        @Nested
        class 포인트_검증에_실패한_후_409_Conflict_예외가_발생한다 {

            @DisplayName("포인트가 부족하다면")
            @Test
            void whenPointIsNotEnough() {
                // arrange
                int current = 30000;
                int amount = 50000;

                // act
                Point point = new Point(1L, current);

                CoreException exception = assertThrows(CoreException.class, () -> point.use(amount));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.CONFLICT);
            }
        }
    }
}
