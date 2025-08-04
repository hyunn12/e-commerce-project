package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.loopers.support.utils.Validation.Message.MESSAGE_PAYMENT_AMOUNT;

@Getter
@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "payment_amount", nullable = false)
    private int paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Builder(builderMethodName = "createBuilder")
    public Payment(Long userId, int paymentAmount) {
        if (paymentAmount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_PAYMENT_AMOUNT);
        }
        this.userId = userId;
        this.paymentAmount = paymentAmount;
        this.status = PaymentStatus.SUCCESS;
    }

    public void markFail() {
        this.status = PaymentStatus.FAIL;
    }

    public void markCancel() {
        this.status = PaymentStatus.CANCEL;
    }
}
