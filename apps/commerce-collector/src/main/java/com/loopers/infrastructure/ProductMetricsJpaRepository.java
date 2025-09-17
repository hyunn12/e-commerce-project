package com.loopers.infrastructure;

import com.loopers.batch.dto.WeeklyRankingAggregateResult;
import com.loopers.domain.metrics.ProductMetrics;
import com.loopers.domain.metrics.ProductMetricsId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetrics, ProductMetricsId> {

    Optional<ProductMetrics> findByIdProductIdAndIdDate(Long productId, LocalDate date);

    @Query("""
        SELECT new com.loopers.batch.dto.WeeklyRankingAggregateResult(
            pm.id.productId,
            SUM(pm.salesCount),
            SUM(pm.likeCount),
            SUM(pm.viewCount)
        )
        FROM ProductMetrics pm
        WHERE pm.updatedAt BETWEEN :startDateTime AND :endDateTime
        GROUP BY pm.id.productId
        """)
    Page<WeeklyRankingAggregateResult> findAggregatedByUpdatedAtBetween(
            @Param("startDateTime") ZonedDateTime startDateTime,
            @Param("endDateTime") ZonedDateTime endDateTime,
            Pageable pageable
    );

    @Modifying
    @Query(value = """
    INSERT INTO product_metrics (product_id, date, like_count, sales_count, view_count)
    VALUES (
        :productId,
        CURDATE(),
        GREATEST(:likeCount, 0),
        GREATEST(:salesCount, 0),
        GREATEST(:viewCount, 0)
    )
    ON DUPLICATE KEY UPDATE
        like_count = GREATEST(like_count + :likeCount, 0),
        sales_count = GREATEST(sales_count + :salesCount, 0),
        view_count = GREATEST(view_count + :viewCount, 0)
    """, nativeQuery = true)
    void upsert(
            @Param("productId") Long productId,
            @Param("likeCount") int likeCount,
            @Param("salesCount") int salesCount,
            @Param("viewCount") int viewCount
    );
}
