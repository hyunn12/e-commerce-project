package com.loopers.infrastructure.payment;

import com.loopers.application.payment.PaymentAlertSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PaymentAlertSenderImpl implements PaymentAlertSender {

    @Value("${client.pg-simulator.x-user-id}")
    private Long userId;

    @Override
    public void sendFail(Map<String, Object> params, Exception e) {
        log.info("PG 결제 실패 알림 전송");
        log.error("[PG 콜백 처리 실패]\n {}",
                String.format(
                        "userId=%s, transactionKey=%s\nerror=%s",
                        userId,
                        params.get("transactionKey"),
                        e.getLocalizedMessage()
                ));
    }
}
