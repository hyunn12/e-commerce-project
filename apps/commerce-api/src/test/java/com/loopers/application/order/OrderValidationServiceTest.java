package com.loopers.application.order;

import com.loopers.application.order.dto.OrderCommand;
import com.loopers.domain.event.CouponEventPublisher;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderValidationServiceTest {

    @Mock
    private CouponUseService couponUseService;
    @Mock
    private StockService stockService;
    @Mock
    private PointUseService pointUseService;
    @Mock
    private CouponEventPublisher couponEventPublisher;

    @InjectMocks
    private OrderValidationService orderValidationService;

    @DisplayName("쿠폰/재고/포인트가 정상적으로 차감되면 주문 엔티티에 반영된다.")
    @Test
    void whenValidSuccess_thenOrderUpdated() {
        // arrange
        Long userId = 1L;
        Long userCouponId = 1L;
        Long productId = 1L;
        int quantity = 2;
        int amount = 5000;
        int discountAmount = 3000;
        int usePoint = 1000;

        OrderItem item = OrderItem.of(productId, quantity, amount);
        Order order = Order.create(userId, userCouponId, List.of(item));

        OrderCommand.Create command = OrderCommand.Create.builder()
                .userId(userId)
                .userCouponId(userCouponId)
                .point(usePoint)
                .items(List.of(OrderCommand.Item.builder()
                        .productId(productId)
                        .quantity(quantity)
                        .amount(amount)
                        .build()))
                .build();
        when(couponUseService.calculateDiscountAmount(anyLong(), anyLong(), anyInt())).thenReturn(discountAmount);
        doNothing().when(couponEventPublisher).publish(any());

        // act
        orderValidationService.validate(command, order);

        // assert
        verify(couponUseService).calculateDiscountAmount(userCouponId, userId, order.getTotalAmount());
        verify(stockService).decrease(productId, quantity);
        verify(pointUseService).useWithLock(userId, usePoint, order.getId());

        assertThat(order.getDiscountAmount()).isEqualTo(discountAmount);
        assertThat(order.getPointAmount()).isEqualTo(usePoint);
    }

    @DisplayName("재고 차감 중 예외가 발생하면 변경값이 반영되지 않는다.")
    @Test
    void whenStockDecreaseFails_thenExceptionThrown() {
        // arrange
        Long userId = 1L;
        Long userCouponId = 1L;
        Long productId = 1L;
        int quantity = 2;
        int amount = 5000;
        int discountAmount = 3000;
        int usePoint = 1000;

        OrderItem item = OrderItem.of(productId, quantity, amount);
        Order order = Order.create(userId, userCouponId, List.of(item));

        OrderCommand.Create command = OrderCommand.Create.builder()
                .userId(userId)
                .userCouponId(userCouponId)
                .point(usePoint)
                .items(List.of(OrderCommand.Item.builder()
                        .productId(productId)
                        .quantity(quantity)
                        .amount(amount)
                        .build()))
                .build();

        when(couponUseService.calculateDiscountAmount(anyLong(), anyLong(), anyInt())).thenReturn(discountAmount);
        doThrow(new RuntimeException("재고 차감 실패")).when(stockService).decrease(anyLong(), anyInt());
        doNothing().when(couponEventPublisher).publish(any());

        // act & assert
        assertThrows(RuntimeException.class, () -> orderValidationService.validate(command, order));
        verify(couponUseService).calculateDiscountAmount(userCouponId, userId, order.getTotalAmount());
        verify(stockService).decrease(productId, quantity);
        verifyNoInteractions(pointUseService);
    }

    @DisplayName("포인트 차감 중 예외가 발생하면 변경값이 반영되지 않는다.")
    @Test
    void whenPointUseFails_thenExceptionThrown() {
        // arrange
        Long userId = 1L;
        Long userCouponId = 1L;
        Long productId = 1L;
        int quantity = 2;
        int amount = 5000;
        int discountAmount = 3000;
        int usePoint = 1000;

        OrderItem item = OrderItem.of(productId, quantity, amount);
        Order order = Order.create(userId, userCouponId, List.of(item));

        OrderCommand.Create command = OrderCommand.Create.builder()
                .userId(userId)
                .userCouponId(userCouponId)
                .point(usePoint)
                .items(List.of(OrderCommand.Item.builder()
                        .productId(productId)
                        .quantity(quantity)
                        .amount(amount)
                        .build()))
                .build();

        when(couponUseService.calculateDiscountAmount(anyLong(), anyLong(), anyInt())).thenReturn(discountAmount);
        doThrow(new RuntimeException("포인트 차감 실패")).when(pointUseService).useWithLock(anyLong(), anyInt(), anyLong());
        doNothing().when(couponEventPublisher).publish(any());

        // act & assert
        assertThrows(RuntimeException.class, () -> orderValidationService.validate(command, order));
        verify(couponUseService).calculateDiscountAmount(userCouponId, userId, order.getTotalAmount());
        verify(stockService).decrease(productId, quantity);
        verify(pointUseService).useWithLock(userId, usePoint, order.getId());
    }
}
