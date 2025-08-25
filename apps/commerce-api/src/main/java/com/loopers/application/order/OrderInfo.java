package com.loopers.application.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInfo {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Main {
        private Long id;
        private String orderNo;
        private int totalAmount;
        private int discountAmount;
        private int pointAmount;
        private OrderStatus status;
        private List<Item> items;

        public static Main from(Order order) {
            List<Item> items = order.getOrderItems().stream()
                    .map(Item::from)
                    .toList();

            return new Main(
                    order.getId(),
                    order.getOrderNo().toString(),
                    order.getTotalAmount(),
                    order.getDiscountAmount(),
                    order.getPointAmount(),
                    order.getStatus(),
                    items
            );
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Item {
        private Long productId;
        private int quantity;
        private int amount;
        private int subtotal;

        public static Item from(OrderItem orderItem) {
            return new Item(
                    orderItem.getProductId(),
                    orderItem.getQuantity(),
                    orderItem.getAmount(),
                    orderItem.getSubtotal()
            );
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Summary {
        private List<Main> orders;
        private int page;
        private int size;

        public static Summary from(Page<Order> orderPage) {
            List<Main> orders = orderPage.getContent().stream()
                    .map(Main::from)
                    .toList();

            return new Summary(orders, orderPage.getTotalPages(), orderPage.getSize());
        }

        public static Summary empty() {
            return new Summary(Collections.emptyList(), 0, 0);
        }
    }
}
