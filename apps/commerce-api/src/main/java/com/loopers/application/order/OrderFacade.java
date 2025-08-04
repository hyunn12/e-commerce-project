package com.loopers.application.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final ProductService productService;
    private final PointService pointService;
    private final PaymentService paymentService;
    private final ExternalOrderSender externalOrderSender;

    @Transactional
    public OrderInfo.Main create(OrderCommand.Create command) {

        // 주문 정보 저장
        Order order = orderService.create(command.toOrderDomain());

        // 상품 재고 조회 및 차감
        for (OrderItem item : order.getOrderItems()) {
            productService.decreaseStock(item.getProductId(), item.getQuantity());
        }

        // 포인트 조회 및 차감
        pointService.use(command.getUserId(), order.getTotalAmount());

        // 결제내역 저장
        paymentService.save(command.getUserId(), order.getTotalAmount());

        // 주문 정보 전달
        externalOrderSender.send(order);

        // 주문 상태 변경
        orderService.markStatus(order, OrderStatus.SUCCESS);

        return OrderInfo.Main.from(order);
    }

    @Transactional
    public OrderInfo.Summary getList(OrderCommand.Summary command) {
        Page<Order> orders = orderService.getList(command.getUserId(), command.getStatus(), command.toPageable());
        return OrderInfo.Summary.from(orders);
    }

    public OrderInfo.Main getDetail(OrderCommand.Detail command) {
        return OrderInfo.Main.from(orderService.getDetail(command.getOrderId()));
    }
}
