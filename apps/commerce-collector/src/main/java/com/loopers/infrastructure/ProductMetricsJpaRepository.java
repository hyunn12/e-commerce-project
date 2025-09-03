package com.loopers.infrastructure;

import com.loopers.domain.ProductMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetrics, Long> {
}
