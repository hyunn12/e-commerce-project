package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentDomainTest {

    @DisplayName("정상적인 결제 금액이라면 객체가 생성된다.")
    @Test
    void createPayment_whenValidPaymentAmount() {
        // arrange
        Long userId = 1L;
        int paymentAmount = 10000;

        // act
        Payment payment = Payment.createBuilder()
                .userId(userId)
                .paymentAmount(paymentAmount)
                .build();

        // assert
        assertThat(payment.getUserId()).isEqualTo(userId);
        assertThat(payment.getPaymentAmount()).isEqualTo(paymentAmount);
    }

    @DisplayName("결제 금액이 0 이하라면 400 Bad Request 예외가 발생한다.")
    @Test
    void throwBadRequestException_whenAmountIsZeroOrNegative() {
        // arrange
        Long userId = 1L;
        int paymentAmount = 0;

        // act
        CoreException exception = assertThrows(CoreException.class, () ->
                Payment.createBuilder()
                        .userId(userId)
                        .paymentAmount(paymentAmount)
                        .build()
        );

        // assert
        assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("markFail 호출한다면 상태가 FAIL 로 변경된다.")
    @Test
    void changeStatusFAIL_whenMarkFailCalled() {
        // arrange
        Payment payment = Payment.createBuilder()
                .userId(1L)
                .paymentAmount(10000)
                .build();

        // act
        payment.markFail();

        // assert
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAIL);
    }

    @DisplayName("markCancel 호출한다면 상태가 CANCEL 로 변경된다.")
    @Test
    void changeStatusCANCEL_whenMarkCancelCalled() {
        // arrange
        Payment payment = Payment.createBuilder()
                .userId(1L)
                .paymentAmount(10000)
                .build();

        // act
        payment.markCancel();

        // assert
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCEL);
    }
}
