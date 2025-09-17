package com.loopers.domain.ranking;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Builder
@Getter
@Entity
@Table(
        name = "mv_product_rank_monthly",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "yearMonth"})
)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMonthlyRanking extends BaseEntity {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "year_month")
    private String yearMonth; // 형식: yyyyMM

    @Column(name = "total_score", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalScore;

    @Column(name = "sales_count", nullable = false)
    private Long salesCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "aggregated_at", nullable = false)
    private ZonedDateTime aggregatedAt;

    public static ProductMonthlyRanking of(Long productId, String yearMonth, BigDecimal totalScore, Long salesCount, Long likeCount, Long viewCount) {
        return ProductMonthlyRanking.builder()
                .productId(productId)
                .yearMonth(yearMonth)
                .totalScore(totalScore)
                .salesCount(salesCount)
                .likeCount(likeCount)
                .viewCount(viewCount)
                .aggregatedAt(ZonedDateTime.now())
                .build();
    }
}
