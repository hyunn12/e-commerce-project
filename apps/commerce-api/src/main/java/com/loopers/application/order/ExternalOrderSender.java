package com.loopers.application.order;

import com.loopers.domain.order.Order;

public interface ExternalOrderSender {

    void send(Order order);
}
