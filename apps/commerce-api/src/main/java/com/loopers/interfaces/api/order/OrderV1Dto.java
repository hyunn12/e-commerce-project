package com.loopers.interfaces.api.order;

import com.loopers.application.order.dto.OrderCommand;
import com.loopers.application.order.dto.OrderInfo;
import com.loopers.domain.order.OrderStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Collections;
import java.util.List;

import static com.loopers.support.utils.Validation.Message.MESSAGE_PAGINATION_PAGE;
import static com.loopers.support.utils.Validation.Message.MESSAGE_PAGINATION_SIZE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderV1Dto {

    public static class OrderRequest {

        public record Create(
                List<OrderCommand.Item> items,
                Long userCouponId,
                int point
        ) {
            public OrderCommand.Create toCommand(Long userId) {
                return OrderCommand.Create.builder()
                        .userId(userId)
                        .userCouponId(userCouponId)
                        .items(items)
                        .point(point)
                        .build();
            }
        }

        public record Summary(
                OrderStatus status,
                @Min(value = 0, message = MESSAGE_PAGINATION_PAGE)
                int page,
                @Min(value = 10, message = MESSAGE_PAGINATION_SIZE)
                @Max(value = 50, message = MESSAGE_PAGINATION_SIZE)
                int size
        ) {
            public OrderCommand.Summary toCommand(Long userId) {
                return OrderCommand.Summary.builder()
                        .userId(userId)
                        .status(status)
                        .page(page)
                        .size(size)
                        .build();
            }
        }
    }

    public static class OrderResponse {

        public record Main(
                Long id,
                String orderNo,
                int totalAmount,
                int discountAmount,
                int pointAmount,
                String status,
                List<OrderInfo.Item> items
        ) {
            public static OrderResponse.Main from(OrderInfo.Main info) {
                return new OrderResponse.Main(
                        info.getId(),
                        info.getOrderNo(),
                        info.getTotalAmount(),
                        info.getDiscountAmount(),
                        info.getPointAmount(),
                        info.getStatus().toString(),
                        info.getItems()
                );
            }
        }

        public record Summary(
                List<OrderResponse.Main> orders,
                int page,
                int size
        ) {
            public static OrderResponse.Summary from(OrderInfo.Summary info) {
                List<OrderResponse.Main> orders = info.getOrders().stream()
                        .map(Main::from)
                        .toList();

                return new OrderResponse.Summary(orders, info.getPage(), info.getSize());
            }

            public static OrderResponse.Summary empty() {
                return new OrderResponse.Summary(Collections.emptyList(), 0, 0);
            }
        }
    }
}
