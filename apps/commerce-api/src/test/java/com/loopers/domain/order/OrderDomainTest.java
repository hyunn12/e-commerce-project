package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderDomainTest {

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 주문_객체_생성_시 {

        @DisplayName("정상적인 orderItem 이 주어진다면 총액이 바르게 계산된다.")
        @Test
        void returnTotalAmount_whenValidOrderItem() {
            // arrange
            OrderItem item1 = OrderItem.of(1L, 2, 10000);
            OrderItem item2 = OrderItem.of(2L, 3, 20000);
            List<OrderItem> items = List.of(item1, item2);

            // act
            Order order = Order.create(1L, items);

            // assert
            assertThat(order.getTotalAmount()).isEqualTo((item1.getQuantity() * item1.getAmount()) + (item2.getQuantity() * item2.getAmount()));
        }

        @DisplayName("최초 status 는 INIT 이다.")
        @Test
        void returnINITStatus_whenValidOrderItem() {
            // arrange
            OrderItem item = OrderItem.of(1L, 1, 10000);

            // act
            Order order = Order.create(1L, List.of(item));

            // assert
            assertThat(order.getStatus()).isEqualTo(OrderStatus.INIT);
        }

        @DisplayName("Order 에 OrderItem 이 연결된다.")
        @Test
        void mapItemToOrder() {
            // arrange
            OrderItem item1 = OrderItem.of(1L, 2, 10000);
            OrderItem item2 = OrderItem.of(2L, 3, 20000);

            // act
            Order order = Order.create(1L, List.of(item1, item2));

            // assert
            assertThat(order.getOrderItems()).hasSize(2);
            assertThat(order.getOrderItems()).contains(item1);
            assertThat(item1.getOrder()).isEqualTo(order);
        }

        @DisplayName("OrderItem 이 비어있다면 400 Bad Request 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenOrderItemIsEmpty() {
            // arrange
            List<OrderItem> emptyItems = List.of();

            // act
            CoreException exception = assertThrows(CoreException.class, () -> Order.create(1L, emptyItems));

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 상태_변경_시 {

        @DisplayName("상태가 SUCCESS 로 변경된다.")
        @Test
        void changeStatusSUCCESS() {
            // arrange
            Order order = Instancio.create(Order.class);

            // act
            order.markSuccess();

            // assert
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SUCCESS);
        }

        @DisplayName("상태가 CANCEL 로 변경된다.")
        @Test
        void changeStatusCANCEL() {
            // arrange
            Order order = Instancio.create(Order.class);

            // act
            order.markCancel();

            // assert
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        }

        @DisplayName("상태가 FAIL 로 변경된다.")
        @Test
        void changeStatusFAIL() {
            // arrange
            Order order = Instancio.create(Order.class);

            // act
            order.markFail();

            // assert
            assertThat(order.getStatus()).isEqualTo(OrderStatus.FAIL);
        }
    }
}
