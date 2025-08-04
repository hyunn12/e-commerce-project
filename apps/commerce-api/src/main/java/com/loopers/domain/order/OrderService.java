package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.loopers.support.utils.Validation.Message.MESSAGE_ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public void markSuccess(Order order) {
        order.markSuccess();
    }

    @Transactional
    public void markCancel(Order order) {
        order.markCancel();
    }

    @Transactional
    public void markFail(Order order) {
        order.markFail();
    }

    public Order getDetail(Long orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_ORDER_NOT_FOUND);
        }
        return order;
    }

    public Page<Order> getList(Long userId, OrderStatus status, Pageable pageable) {
        if (status == null) {
            return orderRepository.findAllByUserId(userId, pageable);
        }
        return orderRepository.findAllByUserIdAndStatus(userId, status, pageable);
    }
}
