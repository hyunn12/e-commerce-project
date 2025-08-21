package com.loopers.domain.payment;

import com.loopers.application.payment.PaymentProcessor;
import com.loopers.domain.payment.dto.PaymentRequest;
import com.loopers.domain.payment.dto.PaymentResponse;
import com.loopers.infrastructure.client.pg.PgClient;
import com.loopers.infrastructure.client.pg.PgClientDto;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardPaymentProcessor implements PaymentProcessor {

    private final PgClient pgClient;
    private final PaymentService paymentService;

    @Value("${client.pg-simulator.callback-url.ver-1}")
    private String callbackUrl;

    @Value("${client.pg-simulator.x-user-id}")
    private Long userId;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void process(PaymentRequest request) {
        Payment payment = paymentService.getDetail(request.getPaymentId());

        // 결제 API 호출
        ApiResponse<PgClientDto.PgResponse> apiResponse = pgClient.request(request.toPgRequest(callbackUrl), userId);
        log.info("결제 요청 결과: {}", apiResponse.meta().result());

        if (apiResponse.meta().result().equals(ApiResponse.Metadata.Result.FAIL)) {
            log.error("결제 요청 실패: errorCode={}, message={}", apiResponse.meta().errorCode(), apiResponse.meta().message());
            payment.setPaymentResponse(PaymentStatus.FAILED, null, apiResponse.meta().message());
            return;
        }
        PaymentResponse response = PaymentResponse.from(apiResponse.data());
        payment.setPaymentResponse(PaymentStatus.PENDING, response.getTransactionKey(), null);
    }
}
