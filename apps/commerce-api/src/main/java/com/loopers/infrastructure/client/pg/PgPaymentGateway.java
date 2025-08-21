package com.loopers.infrastructure.client.pg;

import com.loopers.application.payment.PaymentGateway;
import com.loopers.domain.payment.dto.PaymentRequest;
import com.loopers.domain.payment.dto.PaymentResponse;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PgPaymentGateway implements PaymentGateway {

    private final PgClient pgClient;

    @Value("${client.pg-simulator.x-user-id}")
    private Long userId;

    @Override
    public PaymentResponse requestPayment(PaymentRequest request, String callbackUrl) {
        ApiResponse<PgClientDto.PgResponse> apiResponse = pgClient.request(request.toPgRequest(callbackUrl), userId);
        log.info("결제 요청 결과: {}", apiResponse.meta().result());

        if (apiResponse.meta().result().equals(ApiResponse.Metadata.Result.FAIL)) {
            log.error("결제 요청 실패: errorCode={}, message={}", apiResponse.meta().errorCode(), apiResponse.meta().message());
            return PaymentResponse.fail(apiResponse.meta().message());
        }
        return PaymentResponse.from(apiResponse.data());
    }

    @Override
    public PaymentResponse getTransaction(String transactionKey) {
        ApiResponse<PgClientDto.PgResponse> apiResponse = pgClient.getTransaction(transactionKey, userId);
        log.info("결제 조회 결과: {}", apiResponse.meta().result());

        if (apiResponse.meta().result().equals(ApiResponse.Metadata.Result.FAIL)) {
            log.error("결제 조회 실패: errorCode={}, message={}", apiResponse.meta().errorCode(), apiResponse.meta().message());
            return PaymentResponse.fail(apiResponse.meta().message());
        }

        return PaymentResponse.from(apiResponse.data());
    }

    @Override
    public List<PaymentResponse> getTransactionsByOrder(String orderId) {
        ApiResponse<PgClientDto.OrderResponse> apiResponse = pgClient.getTransactionsByOrder(orderId, userId);
        log.info("주문 전체 결제 조회 결과: {}", apiResponse.meta().result());

        if (apiResponse.meta().result().equals(ApiResponse.Metadata.Result.FAIL)) {
            log.error("주문 전체 결제 조회 실패: errorCode={}, message={}", apiResponse.meta().errorCode(), apiResponse.meta().message());
            return Collections.emptyList();
        }

        return PaymentResponse.from(apiResponse.data());
    }
}
