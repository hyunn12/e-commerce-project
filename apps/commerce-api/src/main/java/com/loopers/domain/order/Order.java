package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.loopers.support.utils.Validation.Message.MESSAGE_ORDER_ITEM_EMPTY;
import static com.loopers.support.utils.Validation.Message.MESSAGE_ORDER_TOTAL_AMOUNT;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder(builderMethodName = "createBuilder")
    public Order(Long userId, int totalAmount) {
        if (totalAmount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_ORDER_TOTAL_AMOUNT);
        }

        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.INIT;
    }

    public static Order create(Long userId, List<OrderItem> items) {
        if (items.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_ORDER_ITEM_EMPTY);
        }

        int totalAmount = items.stream().mapToInt(OrderItem::getSubtotal).sum();

        Order order = Order.createBuilder()
                .userId(userId)
                .totalAmount(totalAmount)
                .build();

        for (OrderItem item : items) {
            order.addItem(item);
        }

        return order;
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.orderItems.add(item);
    }

    public void markSuccess() {
        this.status = OrderStatus.SUCCESS;
    }

    public void markCancel() {
        this.status = OrderStatus.CANCEL;
    }

    public void markFail() {
        this.status = OrderStatus.FAIL;
    }
}
