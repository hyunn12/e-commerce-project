package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.*;

import static com.loopers.support.utils.Validation.Message.MESSAGE_STOCK_INVALID_AMOUNT;
import static com.loopers.support.utils.Validation.Message.MESSAGE_STOCK_NOT_ENOUGH;

@Getter
@Builder
@Entity
@Table(name = "stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Stock extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    public void decrease(int amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_STOCK_INVALID_AMOUNT);
        }

        if (quantity < amount) {
            throw new CoreException(ErrorType.CONFLICT, MESSAGE_STOCK_NOT_ENOUGH);
        }

        this.quantity -= amount;
    }
}

