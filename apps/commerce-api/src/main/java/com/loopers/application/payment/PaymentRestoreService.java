package com.loopers.application.payment;

import com.loopers.application.order.PointUseService;
import com.loopers.application.order.StockService;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.userCoupon.UserCoupon;
import com.loopers.domain.userCoupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentRestoreService {

    private final UserCouponService userCouponService;
    private final StockService stockService;
    private final PointUseService pointUseService;

    public void restore(Order order) {
        // 쿠폰 원복
        UserCoupon userCoupon = userCouponService.getDetail(order.getUserCouponId(), order.getUserId());
        userCoupon.restore();

        // 상품 원복
        for (OrderItem item : order.getOrderItems()) {
            stockService.increase(item.getProductId(), item.getQuantity());
        }

        // 포인트 원복
        pointUseService.restoreWithLock(order.getUserId(), order.getPointAmount(), order.getId());
    }
}
