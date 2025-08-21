package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.*;

import static com.loopers.support.utils.Validation.Message.MESSAGE_PAYMENT_AMOUNT;

@Getter
@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_amount", nullable = false)
    private int paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @Setter
    @Column(name = "transaction_key")
    private String transactionKey;

    @Setter
    @Column(name = "reason")
    private String reason;

    @Builder(builderMethodName = "createBuilder")
    public Payment(Long userId, Long orderId, int paymentAmount, PaymentMethod method) {
        if (paymentAmount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, MESSAGE_PAYMENT_AMOUNT);
        }
        this.userId = userId;
        this.orderId = orderId;
        this.paymentAmount = paymentAmount;
        this.status = PaymentStatus.INIT;
        this.method = method;
    }

    public void setPaymentResponse(PaymentStatus status, String transactionKey, String reason) {
        this.status = status;
        this.transactionKey = transactionKey;
        this.reason = reason;
    }

    public void markPending() {
        this.status = PaymentStatus.PENDING;
    }

    public void markSuccess() {
        this.status = PaymentStatus.SUCCESS;
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void markCanceled() {
        this.status = PaymentStatus.CANCELED;
    }
}
