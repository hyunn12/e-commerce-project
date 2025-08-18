package com.loopers.application.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCommand {

    @Getter
    @Builder
    public static class Create {
        private Long userId;
        private List<Item> items;
        private Long userCouponId;
        private int point;

        public Order toOrderDomain() {
            List<OrderItem> orderItems = items.stream()
                    .map(item -> OrderItem.of(item.getProductId(), item.getQuantity(), item.getAmount()))
                    .toList();

            return Order.create(userId, userCouponId, orderItems);
        }
    }

    @Getter
    @Builder
    public static class Summary {
        private Long userId;
        private OrderStatus status;
        private int page;
        private int size;

        public Pageable toPageable() {
            return PageRequest.of(page, size);
        }
    }

    @Getter
    @Builder
    public static class Detail {
        private Long orderId;
    }

    @Getter
    @Builder
    public static class Item {
        private Long productId;
        private int quantity;
        private int amount;
    }
}
