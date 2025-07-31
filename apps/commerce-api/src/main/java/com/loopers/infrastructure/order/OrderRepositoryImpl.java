package com.loopers.infrastructure.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public OrderItem saveItem(OrderItem orderItem) {
        return orderItemJpaRepository.save(orderItem);
    }

    @Override
    public Order findById(Long orderId) {
        return orderJpaRepository.findById(orderId).orElse(null);
    }

    @Override
    public Page<Order> findAllByUserId(Long userId, Pageable pageable) {
        return orderJpaRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Page<Order> findAllByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable) {
        return orderJpaRepository.findAllByUserIdAndStatus(userId, status, pageable);
    }
}
