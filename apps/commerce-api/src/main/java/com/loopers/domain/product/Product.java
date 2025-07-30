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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int likeCount;

    @Builder(builderMethodName = "createBuilder")
    public Product(Brand brand, String name, int price) {
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.likeCount = 0;
    }

    public void increaseLike() {
        this.likeCount += 1;
    }

    public void decreaseLike() {
        if (this.likeCount > 0) this.likeCount -= 1;
    }
}

