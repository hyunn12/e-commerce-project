package com.loopers.infrastructure.order;

import com.loopers.application.order.ExternalOrderSender;
import com.loopers.domain.order.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExternalOrderSenderImpl implements ExternalOrderSender {
    @Override
    public void send(Order order) {
        log.info("외부 시스템 주문 전송: orderId={}, amount={}", order.getId(), order.getTotalAmount());
    }
}
