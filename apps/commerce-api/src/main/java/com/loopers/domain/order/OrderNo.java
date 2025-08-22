package com.loopers.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderNo {

    @Column(name = "order_no", nullable = false, columnDefinition = "BINARY(16)")
    private UUID orderNo;

    public static OrderNo create() {
        return new OrderNo(UUID.randomUUID());
    }

    public static OrderNo from(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("주문번호가 없습니다.");
        }

        UUID uuid = UUID.fromString(raw);
        return new OrderNo(uuid);
    }

    @Override
    public String toString() {
        return orderNo.toString();
    }
}
