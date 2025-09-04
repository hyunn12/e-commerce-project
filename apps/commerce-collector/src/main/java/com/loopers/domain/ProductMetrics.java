package com.loopers.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Entity
@Table(name = "product_metrics")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetrics {

    @EmbeddedId
    private ProductMetricsId id;

    @Column(name = "sales_count", nullable = false)
    private long salesCount = 0L;

    @Column(name = "like_count", nullable = false)
    private long likeCount = 0L;

    @Column(name = "view_count", nullable = false)
    private long viewCount = 0L;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id.getDate() == null) {
            id.setDate(LocalDate.now());
        }
        updatedAt = ZonedDateTime.now();
    }
}
