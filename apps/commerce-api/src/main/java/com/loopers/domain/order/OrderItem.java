package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.*;

import static com.loopers.support.utils.Validation.Message.MESSAGE_ORDER_ITEM_AMOUNT;
import static com.loopers.support.utils.Validation.Message.MESSAGE_ORDER_ITEM_QUANTITY;

@Getter
@Entity
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "subtotal", nullable = false)
    private int subtotal;

    protected void setOrder(Order order) {
        this.order = order;
    }

    @Builder(builderMethodName = "createBuilder")
    public OrderItem(Long productId, int quantity, int amount) {
        if (quantity <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_ORDER_ITEM_QUANTITY);
        }
        if (amount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_ORDER_ITEM_AMOUNT);
        }

        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
        this.subtotal = quantity * amount;
    }

    public static OrderItem of(Long productId, int quantity, int amount) {
        return OrderItem.createBuilder()
                .productId(productId)
                .quantity(quantity)
                .amount(amount)
                .build();
    }
}
