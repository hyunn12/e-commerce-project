package com.loopers.application.order;

import com.loopers.application.order.dto.OrderCommand;
import com.loopers.domain.event.CouponEventPublisher;
import com.loopers.domain.event.dto.CouponUseEvent;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderValidationService {

    private final CouponUseService couponUseService;
    private final StockService stockService;
    private final PointUseService pointUseService;
    private final CouponEventPublisher couponEventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void validate(OrderCommand.Create command, Order order) {
        // 쿠폰 조회 및 사용
        int discountAmount = 0;
        if (command.getUserCouponId() != null) {
            discountAmount = couponUseService.calculateDiscountAmount(command.getUserCouponId(), command.getUserId(), order.getTotalAmount());
            couponEventPublisher.publish(CouponUseEvent.of(command.getUserCouponId(), command.getUserId()));
        }
        order.setDiscountAmount(discountAmount);

        // 상품 재고 조회 및 차감
        for (OrderItem item : order.getOrderItems()) {
            stockService.decrease(item.getProductId(), item.getQuantity());
        }

        // 포인트 조회 및 차감
        pointUseService.useWithLock(command.getUserId(), command.getPoint(), order.getId());
        order.setPointAmount(command.getPoint());
    }
}
