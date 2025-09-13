package com.loopers.interfaces.consumer;

import com.loopers.domain.metrics.ProductMetrics;
import com.loopers.infrastructure.ProductMetricsJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductMetricsJpaRepositoryTest {

    @Autowired
    private ProductMetricsJpaRepository productMetricsJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Transactional
    @DisplayName("신규 productId upsert 시 insert 된다.")
    @Test
    void upsert_whenNotExists() {
        Long productId = 1L;

        productMetricsJpaRepository.upsert(productId, 1, 2, 3);

        ProductMetrics productMetrics = productMetricsJpaRepository.findByIdProductIdAndIdDate(productId, LocalDate.now()).orElseThrow();
        assertThat(productMetrics.getLikeCount()).isEqualTo(1);
        assertThat(productMetrics.getSalesCount()).isEqualTo(2);
        assertThat(productMetrics.getViewCount()).isEqualTo(3);
    }

    @Transactional
    @DisplayName("기존 productId upsert 시 누적 update 된다.")
    @Test
    void upsert_whenExists() {
        Long productId = 1L;

        productMetricsJpaRepository.upsert(productId, 1, 2, 3);
        productMetricsJpaRepository.upsert(productId, 2, -1, 1);

        ProductMetrics productMetrics = productMetricsJpaRepository.findByIdProductIdAndIdDate(productId, LocalDate.now()).orElseThrow();
        assertThat(productMetrics.getLikeCount()).isEqualTo(3);
        assertThat(productMetrics.getSalesCount()).isEqualTo(1);
        assertThat(productMetrics.getViewCount()).isEqualTo(4);
    }

    @Transactional
    @DisplayName("감소 연산 시 음수로 내려가지 않는다.")
    @Test
    void upsert_doNotBelowZero() {
        Long productId = 1L;

        productMetricsJpaRepository.upsert(productId, -5, -10, -2);

        ProductMetrics productMetrics = productMetricsJpaRepository.findByIdProductIdAndIdDate(productId, LocalDate.now()).orElseThrow();
        assertThat(productMetrics.getLikeCount()).isEqualTo(0);
        assertThat(productMetrics.getSalesCount()).isEqualTo(0);
        assertThat(productMetrics.getViewCount()).isEqualTo(0);
    }
}
