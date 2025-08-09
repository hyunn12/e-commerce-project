package com.loopers.infrastructure.product;

import com.loopers.domain.product.Stock;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.product.id = :productId")
    @QueryHints(value = {@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<Stock> findByProductIdWithLock(Long productId);

    List<Stock> findAllByProductIdIn(List<Long> productIds);
}
