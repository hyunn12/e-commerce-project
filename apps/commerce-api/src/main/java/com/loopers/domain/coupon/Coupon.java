package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "max_count", nullable = false)
    private int maxCount;

    @Column(name = "issued_count", nullable = false)
    private int issuedCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DiscountType type;

    @Column(name = "discount_value", nullable = false)
    private int discountValue;

    @Column(name = "max_discount_amount")
    private Integer maxDiscountAmount;

    @Column(name = "min_amount", nullable = false)
    private int minAmount;

    public Coupon(String name, int maxCount, int issuedCount, DiscountType type, int discountValue, Integer maxDiscountAmount, int minAmount) {
        this.name = name;
        this.maxCount = maxCount;
        this.issuedCount = issuedCount;
        this.type = type;
        this.discountValue = discountValue;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minAmount = minAmount;
    }

    public boolean isUnderMinAmount(int orderAmount) {
        return orderAmount < minAmount;
    }
}
