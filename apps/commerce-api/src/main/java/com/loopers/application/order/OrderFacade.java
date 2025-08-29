package com.loopers.application.order;

import com.loopers.application.order.dto.OrderCommand;
import com.loopers.application.order.dto.OrderInfo;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.event.dto.OrderCreatedEvent;
import com.loopers.domain.event.OrderEventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final OrderValidationService orderValidateService;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public OrderInfo.Main create(OrderCommand.Create command) {
        Order order = orderService.create(command.toDomain());

        try {
            // 주문 검증
            orderValidateService.validate(command, order);
        } catch (Exception e) {
            log.error("주문 처리 중 예외 발생: {}", e.getLocalizedMessage());
            order.markOrderFailed();
        }

        // 주문 완료 이벤트 발행
        orderEventPublisher.publish(
                OrderCreatedEvent.of(order.getId(), order.getUserId(), order.getPaymentAmount(), command.getMethod(), command.getCardType(), command.getCardNo())
        );

        return OrderInfo.Main.from(order);
    }

    @Transactional
    public OrderInfo.Summary getList(OrderCommand.Summary command) {
        Page<Order> orders = orderService.getList(command.getUserId(), command.getStatus(), command.toPageable());
        return OrderInfo.Summary.from(orders);
    }

    public OrderInfo.Main getDetail(Long orderId) {
        return OrderInfo.Main.from(orderService.getDetail(orderId));
    }
}
