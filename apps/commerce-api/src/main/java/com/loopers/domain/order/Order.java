package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.*;

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

    @Embedded
    private OrderNo orderNo;

    @Column(name = "user_coupon_id")
    private Long userCouponId;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Setter
    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder(builderMethodName = "createBuilder")
    public Order(Long userId, Long userCouponId, int totalAmount) {
        if (totalAmount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_ORDER_TOTAL_AMOUNT);
        }

        this.userId = userId;
        this.orderNo = OrderNo.create();
        this.userCouponId = userCouponId;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.INIT;
    }

    public static Order create(Long userId, Long userCouponId, List<OrderItem> items) {
        if (items.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_ORDER_ITEM_EMPTY);
        }

        int totalAmount = items.stream().mapToInt(OrderItem::getSubtotal).sum();

        Order order = Order.createBuilder().userId(userId).userCouponId(userCouponId).totalAmount(totalAmount).build();

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
