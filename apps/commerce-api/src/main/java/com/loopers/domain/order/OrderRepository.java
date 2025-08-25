package com.loopers.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {

    Order save(Order order);

    Order getDetail(Long orderId);

    Order getDetailWithLock(Long orderId);

    Page<Order> getListByUserId(Long userId, Pageable pageable);

    Page<Order> getListByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);
}
