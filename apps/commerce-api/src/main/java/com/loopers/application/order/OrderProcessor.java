package com.loopers.application.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProcessor {

    private final CouponUseService couponUseService;
    private final StockService stockService;
    private final PointProcessor pointProcessor;

    public void process(OrderCommand.Create command, Order order) {
        // 쿠폰 조회 및 사용
        int discountAmount = 0;
        if (command.getUserCouponId() != null) {
            discountAmount = couponUseService.use(command.getUserCouponId(), command.getUserId(), order.getTotalAmount());
        }
        order.setDiscountAmount(discountAmount);

        // 상품 재고 조회 및 차감
        for (OrderItem item : order.getOrderItems()) {
            stockService.decrease(item.getProductId(), item.getQuantity());
        }

        // 포인트 조회 및 차감
        pointProcessor.useWithLock(command.getUserId(), command.getPoint(), order.getId());
        order.setPointAmount(command.getPoint());
    }
}
