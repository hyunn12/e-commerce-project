package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    private Like(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public static Like of(Long userId, Long productId) {
        return new Like(userId, productId);
    }

    public boolean isDeleted() {
        return this.getDeletedAt() != null;
    }
}
