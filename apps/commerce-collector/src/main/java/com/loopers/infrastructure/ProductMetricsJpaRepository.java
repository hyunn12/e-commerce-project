package com.loopers.infrastructure;

import com.loopers.domain.ProductMetrics;
import com.loopers.domain.ProductMetricsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetrics, ProductMetricsId> {

    @Modifying
    @Query(value = """
        INSERT INTO product_metrics (product_id, date, sales_count, like_count, view_count)
        VALUES (:productId, CURDATE(), 0, 1, 0)
        ON DUPLICATE KEY UPDATE like_count = like_count + 1
        """, nativeQuery = true)
    void increaseLikeCount(@Param("productId") Long productId);

    @Modifying
    @Query(value = """
        INSERT INTO product_metrics (product_id, date, sales_count, like_count, view_count)
        VALUES (:productId, CURDATE(), 0, 0, 0)
        ON DUPLICATE KEY UPDATE like_count = GREATEST(like_count - 1, 0)
        """, nativeQuery = true)
    void decreaseLikeCount(@Param("productId") Long productId);

    @Modifying
    @Query(value = """
        INSERT INTO product_metrics (product_id, date, sales_count, like_count, view_count)
        VALUES (:productId, CURDATE(), :quantity, 0, 0)
        ON DUPLICATE KEY UPDATE sales_count = sales_count + :quantity
        """, nativeQuery = true)
    void increaseSalesCount(@Param("productId") Long productId, @Param("quantity") int quantity);

    @Modifying
    @Query(value = """
        INSERT INTO product_metrics (product_id, date, sales_count, like_count, view_count)
        VALUES (:productId, CURDATE(), 0, 0, 0)
        ON DUPLICATE KEY UPDATE sales_count = GREATEST(sales_count - :quantity, 0)
        """, nativeQuery = true)
    void decreaseSalesCount(@Param("productId") Long productId, @Param("quantity") int quantity);

    @Modifying
    @Query(value = """
        INSERT INTO product_metrics (product_id, date, sales_count, like_count, view_count)
        VALUES (:productId, CURDATE(), 0, 0, 1)
        ON DUPLICATE KEY UPDATE view_count = view_count + 1
        """, nativeQuery = true)
    void increaseViewCount(@Param("productId") Long productId);

    Optional<ProductMetrics> findByIdProductIdAndIdDate(Long productId, LocalDate date);

    boolean existsByIdProductIdAndIdDate(Long productId, LocalDate date);

    @Query(value = "select curdate()", nativeQuery = true)
    LocalDate currentDate();
}
