package com.loopers.application.order;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointHistory;
import com.loopers.domain.point.PointType;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.Stock;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.infrastructure.payment.PaymentJpaRepository;
import com.loopers.infrastructure.point.PointHistoryJpaRepository;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.product.StockJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class OrderFacadeIntegrationTest {
    // orm --
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private StockJpaRepository stockJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut --
    @Autowired
    private OrderFacade orderFacade;

    @MockitoSpyBean
    private ExternalOrderSender externalOrderSender;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    final Long userId = 1L;

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 주문_생성_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 주문이_정상적으로_생성된다 {

            @DisplayName("정상적인 파라미터가 주어진 경우")
            @Test
            void whenValidParameter() {
                // arrange
                final int initQuantity = 10;
                final int decreaseQuantity = 2;
                final int initPoint = 50000;
                final int price = 20000;

                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
                Product product = productJpaRepository.save(Product.createBuilder().name("상품").price(price).brand(brand).build());
                stockJpaRepository.save(new Stock(product, initQuantity));
                pointJpaRepository.save(new Point(userId, initPoint));

                OrderCommand.Create command = OrderCommand.Create.builder()
                        .userId(userId)
                        .items(List.of(OrderCommand.Item.builder()
                                .productId(product.getId())
                                .quantity(decreaseQuantity)
                                .amount(product.getPrice())
                                .build()))
                        .build();

                // act
                OrderInfo.Main result = orderFacade.createOrder(command);

                // assert
                Order order = orderJpaRepository.findById(result.getId()).get();
                assertThat(order.getStatus()).isEqualTo(OrderStatus.SUCCESS);

                verify(externalOrderSender, times(1)).send(any(Order.class));

                Stock stock = stockJpaRepository.findByProductId(product.getId()).get();
                assertThat(stock.getQuantity()).isEqualTo(initQuantity-decreaseQuantity);

                Point point = pointJpaRepository.findByUserId(userId).get();
                assertThat(point.getPoint()).isEqualTo(initPoint-(decreaseQuantity*price));

                List<PointHistory> histories = pointHistoryJpaRepository.findByUserId(userId);
                assertThat(histories).hasSize(1);
                assertThat(histories.get(0).getType()).isEqualTo(PointType.USE);

                List<Payment> payments = paymentJpaRepository.findAll();
                assertThat(payments).hasSize(1);
                assertThat(payments.get(0).getPaymentAmount()).isEqualTo(decreaseQuantity*price);
                assertThat(payments.get(0).getUserId()).isEqualTo(userId);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 주문_생성이_실패하고_409_Conflict_예외가_발생한다 {

            @DisplayName("재고가 부족하다면")
            @Test
            void whenStockIsNotEnough() {
                // arrange
                final int initQuantity = 1;
                final int decreaseQuantity = 2;
                final int initPoint = 50000;
                final int price = 20000;

                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
                Product product = productJpaRepository.save(Product.createBuilder().name("상품").price(price).brand(brand).build());
                stockJpaRepository.save(new Stock(product, initQuantity));
                pointJpaRepository.save(new Point(userId, initPoint));

                OrderCommand.Create command = OrderCommand.Create.builder()
                        .userId(userId)
                        .items(List.of(OrderCommand.Item.builder()
                                .productId(product.getId())
                                .quantity(decreaseQuantity)
                                .amount(product.getPrice())
                                .build()))
                        .build();

                // act
                CoreException exception = assertThrows(CoreException.class, () -> orderFacade.createOrder(command));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.CONFLICT);
            }

            @DisplayName("포인트가 부족하다면")
            @Test
            void whenPointIsNotEnough() {
                // arrange
                final int initQuantity = 10;
                final int decreaseQuantity = 2;
                final int initPoint = 10000;
                final int price = 20000;

                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
                Product product = productJpaRepository.save(Product.createBuilder().name("상품").price(price).brand(brand).build());
                stockJpaRepository.save(new Stock(product, initQuantity));
                pointJpaRepository.save(new Point(userId, initPoint));

                OrderCommand.Create command = OrderCommand.Create.builder()
                        .userId(userId)
                        .items(List.of(OrderCommand.Item.builder()
                                .productId(product.getId())
                                .quantity(decreaseQuantity)
                                .amount(product.getPrice())
                                .build()))
                        .build();

                // act
                CoreException exception = assertThrows(CoreException.class, () -> orderFacade.createOrder(command));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.CONFLICT);
            }
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 주문_목록_조회_시 {

        @Transactional
        @DisplayName("존재하는 userId 라면 주문 목록을 반환한다.")
        @Test
        void returnList_whenValidUserId() {
            // arrange
            int count = 3;
            for (int i = 0; i < count; i++) {
                OrderItem item1 = OrderItem.of(1L, 2, 10000);
                OrderItem item2 = OrderItem.of(2L, 3, 20000);
                List<OrderItem> items = List.of(item1, item2);
                Order order = Order.create(userId, items);
                order.markSuccess();
                orderJpaRepository.save(order);
            }

            // act
            OrderInfo.Summary result = orderFacade.getList(OrderCommand.Summary.builder()
                    .userId(userId)
                    .page(0)
                    .size(10)
                    .build());

            // assert
            assertThat(result.getOrders()).hasSize(count);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 주문_상세_조회_시 {

        @Transactional
        @DisplayName("정상적인 orderId 라면 주문 정보를 반환한다.")
        @Test
        void returnDetail_whenValidId() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            Product product = productJpaRepository.save(Product.createBuilder().name("상품").price(1000).brand(brand).build());
            stockJpaRepository.save(new Stock(product, 10));

            int quantity = 2;
            Order order = Order.create(userId, List.of(OrderItem.of(product.getId(), quantity, quantity*product.getPrice())));
            order.markSuccess();
            orderJpaRepository.save(order);

            // act
            OrderInfo.Main result = orderFacade.getDetail(OrderCommand.Detail.builder()
                    .orderId(order.getId())
                    .build());

            // assert
            assertThat(result.getId()).isEqualTo(order.getId());
            assertThat(result.getStatus()).isEqualTo(OrderStatus.SUCCESS);
            assertThat(result.getTotalAmount()).isEqualTo(order.getTotalAmount());
            assertThat(result.getItems()).hasSize(1);
        }

        @DisplayName("존재하지 않는 orderId 가 주어진다면 404 Not Found 예외가 발생한다.")
        @Test
        void throwNotFoundException_whenInvalidOrderId() {
            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.getDetail(OrderCommand.Detail.builder()
                    .orderId(999L)
                    .build()));

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
