package com.loopers.domain.payment;

import com.loopers.infrastructure.payment.PaymentJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentServiceIntegrationTest {
    // orm --
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut --
    @Autowired
    private PaymentService paymentService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    private final Long userId = 1L;
    private final int amount = 10000;

    @DisplayName("결제 정보가 정상적으로 저장된다.")
    @Test
    void createPayment() {
        // act
        Payment point = paymentService.create(userId, amount);

        // assert
        Payment result = paymentJpaRepository.findById(point.getId()).get();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getPaymentAmount()).isEqualTo(amount);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @DisplayName("결제 실패 처리한다면 상태가 FAIL 로 변경된다.")
    @Test
    void markFail() {
        // arrange
        Payment payment = paymentService.create(userId, amount);

        // act
        paymentService.markStatus(payment, PaymentStatus.FAIL);

        // assert
        Payment result = paymentJpaRepository.findById(payment.getId()).get();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAIL);
    }

    @DisplayName("결제 취소 처리한다면 상태가 CANCEL 로 변경된다.")
    @Test
    void markCancel() {
        // arrange
        Payment payment = paymentService.create(userId, amount);

        // act
        paymentService.markStatus(payment, PaymentStatus.CANCEL);

        // assert
        Payment result = paymentJpaRepository.findById(payment.getId()).get();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCEL);
    }
}
