package com.loopers.application.payment;

import com.loopers.application.order.ExternalOrderSender;
import com.loopers.application.payment.dto.PaymentCommand;
import com.loopers.application.payment.dto.PaymentInfo;
import com.loopers.domain.event.PaymentEventPublisher;
import com.loopers.domain.event.dto.PaymentFailEvent;
import com.loopers.domain.event.dto.PaymentRequestSuccessEvent;
import com.loopers.domain.event.dto.PaymentSuccessEvent;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.domain.payment.dto.CardType;
import com.loopers.domain.payment.dto.PaymentRequest;
import com.loopers.domain.payment.dto.PaymentResponse;
import com.loopers.domain.payment.dto.PaymentResponseResult;
import com.loopers.infrastructure.client.pg.PgClientDto;
import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.infrastructure.payment.PaymentJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class PaymentFacadeIntegrationTest {

    // orm --
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut--
    @Autowired
    private PaymentFacade paymentFacade;

    @MockitoBean
    private PaymentGatewayService paymentGatewayService;
    @MockitoBean
    private PaymentRestoreService paymentRestoreService;
    @MockitoBean
    private ExternalOrderSender externalOrderSender;
    @MockitoBean
    private PaymentAlertSender paymentAlertSender;
    @MockitoBean
    private PaymentEventPublisher paymentEventPublisher;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    final Long userId = 1L;
    Order order;
    Payment payment;

    @BeforeEach
    void setUp() {
        OrderItem item = OrderItem.of(1L, 1, 10000);
        order = orderJpaRepository.save(Order.create(userId, 1L, List.of(item)));
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 결제_요청_시 {
        PaymentCommand.Create command;

        @BeforeEach
        void setUp() {
            payment = Payment.createBuilder().userId(userId).paymentAmount(order.getTotalAmount()).build();
            command = PaymentCommand.Create.builder()
                    .userId(userId)
                    .orderId(order.getId())
                    .method(PaymentMethod.CARD)
                    .cardType(CardType.SAMSUNG)
                    .cardNo("1111-2222-3333-4444")
                    .paymentAmount(order.getTotalAmount())
                    .build();
        }

        @DisplayName("PG 결제가 성공하면 Payment 는 PENDING 상태가 된다.")
        @Test
        void whenPgSuccess_thenPaymentPending() {
            // arrange
            PgClientDto.PgResponse pgResponse = new PgClientDto.PgResponse(
                    "TR:9577c5",
                    order.getOrderNo().toString(),
                    PaymentResponseResult.SUCCESS,
                    "요청 완료"
            );
            PaymentResponse response = PaymentResponse.from(pgResponse);

            when(paymentGatewayService.requestPayment(any(PaymentRequest.class)))
                    .thenReturn(response);
            doNothing().when(paymentEventPublisher).publish(any(PaymentRequestSuccessEvent.class));


            // act
            PaymentInfo.Main result = paymentFacade.payment(command);

            // assert
            Payment payment = paymentJpaRepository.findById(result.getId()).get();
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(payment.getTransactionKey()).isEqualTo(pgResponse.transactionKey());
            verify(paymentGatewayService).requestPayment(any(PaymentRequest.class));
        }

        @Test
        @DisplayName("주문 상태가 CREATED 가 아닌 경우 결제요청은 실패한다.")
        void whenOrderStatusIsNotCreated_thenFail() {
            // arrange
            order.markWaitingPayment();
            orderJpaRepository.save(order);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> paymentFacade.payment(command));

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 결제_콜백_시 {
        String transactionKey = "TR:9577c5";

        @BeforeEach
        void setUp() {
            order.markWaitingPayment();
            orderJpaRepository.save(order);
            payment = Payment.createBuilder()
                    .userId(userId)
                    .orderId(order.getId())
                    .paymentAmount(order.getTotalAmount())
                    .method(PaymentMethod.CARD)
                    .build();
            payment.setPaymentPending(transactionKey);
            paymentJpaRepository.save(payment);
        }

        @DisplayName("PG 콜백이 SUCCESS 인 경우, Payment는 결제대기 상태로 바뀐다.")
        @Test
        void whenCallbackSuccess_thenPaymentAndOrderSuccess() {
            // arrange
            PaymentCommand.Callback command = PaymentCommand.Callback.builder()
                    .orderNo(order.getOrderNo().toString())
                    .transactionKey(transactionKey)
                    .result(PaymentResponseResult.SUCCESS)
                    .reason("승인 완료")
                    .build();

            when(paymentGatewayService.getTransaction(transactionKey))
                    .thenReturn(PaymentInfo.Callback.from(PaymentResponseResult.SUCCESS, null));
            doNothing().when(paymentEventPublisher).publish(any(PaymentSuccessEvent.class));

            // act
            paymentFacade.paymentCallback(command);

            // assert
            Payment updatedPayment = paymentJpaRepository.findById(payment.getId()).get();
            assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        }

        @Test
        @DisplayName("PG 콜백이 FAIL 인 경우, Payment는 실패 상태로 바뀐다.")
        void whenCallbackFail_thenPaymentAndOrderFailed() {
            // arrange
            PaymentCommand.Callback command = PaymentCommand.Callback.builder()
                    .orderNo(order.getOrderNo().toString())
                    .transactionKey(transactionKey)
                    .result(PaymentResponseResult.FAIL)
                    .reason("한도 초과")
                    .build();

            when(paymentGatewayService.getTransaction(transactionKey))
                    .thenReturn(PaymentInfo.Callback.from(PaymentResponseResult.SUCCESS, null));
            doNothing().when(paymentEventPublisher).publish(any(PaymentFailEvent.class));

            // act
            paymentFacade.paymentCallback(command);

            // assert
            Payment updatedPayment = paymentJpaRepository.findById(payment.getId()).get();
            assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        }
    }
}
