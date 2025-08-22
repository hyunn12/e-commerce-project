package com.loopers.domain.order;

public enum OrderStatus {
    CREATED,
    WAITING_PAYMENT,
    PAID,
    PAYMENT_FAILED,
    ORDER_FAILED,
    CANCELED,
    SHIPPING,
    DELIVERED,
}
