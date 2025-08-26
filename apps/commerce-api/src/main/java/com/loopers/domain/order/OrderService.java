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

    public Order getDetail(Long orderId) {
        Order order = orderRepository.getDetail(orderId);
        if (order == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_ORDER_NOT_FOUND);
        }
        return order;
    }

    public Order getDetailWithLock(Long orderId) {
        Order order = orderRepository.getDetailWithLock(orderId);
        if (order == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_ORDER_NOT_FOUND);
        }
        return order;
    }

    public Page<Order> getList(Long userId, OrderStatus status, Pageable pageable) {
        if (status == null) {
            return orderRepository.getListByUserId(userId, pageable);
        }
        return orderRepository.getListByUserIdAndStatus(userId, status, pageable);
    }

    public void checkInitOrder(Order order, Long userId) {
        if (!order.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.CONFLICT, "사용자 정보가 일치하지 않습니다.");
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new CoreException(ErrorType.CONFLICT, "주문 상태가 대기중이 아닙니다. status: " + order.getStatus());
        }
    }

    public void checkWaitingOrder(Order order) {
        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new CoreException(ErrorType.CONFLICT, "주문 상태가 결제 대기중이 아닙니다. status: " + order.getStatus());
        }
    }
}
