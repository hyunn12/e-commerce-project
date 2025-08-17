package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.brand.Brand;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private SalesStatus status;

    @Builder(builderMethodName = "createBuilder")
    public Product(Brand brand, String name, int price) {
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.likeCount = 0;
        this.status = SalesStatus.ACTIVE;
    }

    public void increaseLike() {
        this.likeCount += 1;
    }

    public void decreaseLike() {
        if (this.likeCount > 0) this.likeCount -= 1;
    }
}

