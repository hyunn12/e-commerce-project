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
            throw new CoreException(ErrorType.BAD_REQUEST, "총금액은 0 보다 커야합니다.");
        }

        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.INIT;
    }

    public static Order create(Long userId, List<OrderItem> items) {
        if (items.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 누락되었습니다.");
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
}
