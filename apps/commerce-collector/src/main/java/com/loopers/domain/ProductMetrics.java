package com.loopers.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Builder
@Entity
@Table(name = "product_metrics")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetrics {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sales_count", nullable = false)
    private Long salesCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    private ProductMetrics(Long productId) {
        this.productId = productId;
    }

    public static ProductMetrics of(Long productId) {
        return new ProductMetrics(productId);
    }

    public void increaseSalesCount() {
        this.salesCount++;
        this.updatedAt = ZonedDateTime.now();
    }
    public void decreaseSalesCount() {
        this.salesCount--;
        this.updatedAt = ZonedDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
        this.updatedAt = ZonedDateTime.now();
    }
    public void decreaseLikeCount() {
        this.likeCount--;
        this.updatedAt = ZonedDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
        this.updatedAt = ZonedDateTime.now();
    }
    public void decreaseViewCount() {
        this.viewCount--;
        this.updatedAt = ZonedDateTime.now();
    }
}
