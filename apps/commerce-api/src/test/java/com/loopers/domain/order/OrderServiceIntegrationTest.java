package com.loopers.domain.order;

import com.loopers.infrastructure.order.OrderItemJpaRepository;
import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OrderServiceIntegrationTest {
    // orm --
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut --
    @Autowired
    private OrderService orderService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    private final Long userId = 1L;

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 주문_생성_시 {

        @DisplayName("유효한 주문이라면 주문이 저장된다.")
        @Test
        void saveOrder_whenValidOrder() {
            // arrange
            OrderItem item = OrderItem.of(1L, 2, 10000);
            Order order = Order.create(userId, List.of(item));

            // act
            Order savedOrder = orderService.create(order);

            // assert
            Optional<Order> findOrder = orderJpaRepository.findById(order.getId());
            assertThat(findOrder).isPresent();
            assertThat(findOrder.get().getId()).isEqualTo(savedOrder.getId());
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 주문_상태_변경_시 {

        @DisplayName("markSuccess 호출한다면 상태가 SUCCESS 로 변경된다.")
        @Test
        void markSuccess_whenCalled() {
            // arrange
            OrderItem item = OrderItem.of(1L, 2, 10000);
            Order order = orderJpaRepository.save(Order.create(userId, List.of(item)));

            // act
            orderService.markSuccess(order);

            // assert
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SUCCESS);
        }

        @DisplayName("markCancel 호출한다면 상태가 CANCEL 로 변경된다.")
        @Test
        void markCancel_whenCalled() {
            // arrange
            OrderItem item = OrderItem.of(1L, 2, 10000);
            Order order = orderJpaRepository.save(Order.create(userId, List.of(item)));

            // act
            orderService.markCancel(order);

            // assert
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        }

        @DisplayName("markFail 호출한다면 상태가 CANCEL 로 변경된다.")
        @Test
        void markFail_whenCalled() {
            // arrange
            OrderItem item = OrderItem.of(1L, 2, 10000);
            Order order = orderJpaRepository.save(Order.create(userId, List.of(item)));

            // act
            orderService.markFail(order);

            // assert
            assertThat(order.getStatus()).isEqualTo(OrderStatus.FAIL);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 주문_목록_조회_시 {

        @DisplayName("status 가 없다면 유저의 전체 주문을 반환한다.")
        @Test
        void returnOrderList_whenStatusIsNull() {
            // arrange
            OrderItem item1 = OrderItem.of(1L, 2, 10000);
            orderJpaRepository.save(Order.create(userId, List.of(item1)));

            OrderItem item2 = OrderItem.of(2L, 3, 20000);
            orderJpaRepository.save(Order.create(userId, List.of(item2)));

            Pageable pageable = PageRequest.of(0, 10);

            // act
            Page<Order> result = orderService.getListByUserId(userId, null, pageable);

            // assert
            assertThat(result.getTotalElements()).isEqualTo(2);
        }

        @DisplayName("status 가 있다면 해당 상태의 주문을 반환한다.")
        @Test
        void returnOrderList_whenStatusIsGiven() {
            // arrange
            OrderItem item1 = OrderItem.of(1L, 2, 10000);
            orderJpaRepository.save(Order.create(userId, List.of(item1)));

            OrderItem item2 = OrderItem.of(2L, 3, 20000);
            Order order = Order.create(userId, List.of(item2));
            order.markCancel();
            orderJpaRepository.save(order);

            Pageable pageable = PageRequest.of(0, 10);

            // act
            Page<Order> result = orderService.getListByUserId(userId, OrderStatus.CANCEL, pageable);

            // assert
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent())
                    .extracting(Order::getStatus)
                    .containsOnly(OrderStatus.CANCEL);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 주문_상세_조회_시 {

        @DisplayName("유효한 orderId 라면 주문 상세 정보를 반환한다.")
        @Test
        void returnOrderDetail_whenValidOrderId() {
            // arrange
            OrderItem item = OrderItem.of(1L, 2, 10000);
            Order order = orderJpaRepository.save(Order.create(userId, List.of(item)));

            // act
            Order result = orderService.getDetail(order.getId());

            // assert
            assertThat(result.getId()).isEqualTo(order.getId());
        }

        @DisplayName("존재하지 않는 orderId 라면 404 Not Found 예외가 발생한다.")
        @Test
        void throwNotFound_whenValidOrderId() {
            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderService.getDetail(Long.MAX_VALUE));

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
