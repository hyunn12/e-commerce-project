package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemDomainTest {

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 주문_아이템_객체_생성_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 객체가_생성된다 {

            @DisplayName("정상적인 값이 주어진다면")
            @Test
            void whenValidValues() {
                // arrange
                Long productId = 1L;
                int quantity = 2;
                int amount = 10000;

                // act
                OrderItem item = OrderItem.of(productId, quantity, amount);

                // assert
                assertThat(item.getProductId()).isEqualTo(productId);
                assertThat(item.getQuantity()).isEqualTo(quantity);
                assertThat(item.getAmount()).isEqualTo(amount);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 객체_생성에_실패한_후_400_Bad_Request_예외가_발생한다 {

            @DisplayName("수량이 0 이라면")
            @Test
            void whenQuantityIsZero() {
                // arrange
                Long productId = 1L;
                int quantity = 0;
                int amount = 10000;

                // act
                CoreException exception = assertThrows(CoreException.class, () -> OrderItem.of(productId, quantity, amount));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }

            @DisplayName("수량이 0 미만이라면")
            @Test
            void whenQuantityIsNegative() {
                // arrange
                Long productId = 1L;
                int quantity = -5;
                int amount = 10000;

                // act
                CoreException exception = assertThrows(CoreException.class, () -> OrderItem.of(productId, quantity, amount));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }

            @DisplayName("금액이 0 미만이라면")
            @Test
            void whenAmountIsNegative() {
                // arrange
                Long productId = 1L;
                int quantity = 2;
                int amount = -1000;

                // act
                CoreException exception = assertThrows(CoreException.class, () -> OrderItem.of(productId, quantity, amount));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 합계가_정확히_계산된다 {

            @DisplayName("정상적인 수량과 금액이 주어진다면")
            @Test
            void whenValidQuantityAndAmount() {
                // arrange
                Long productId = 1L;
                int quantity = 2;
                int amount = 10000;

                // act
                OrderItem item = OrderItem.of(productId, quantity, amount);

                // assert
                assertThat(item.getSubtotal()).isEqualTo(quantity * amount);
            }
        }
    }
}
