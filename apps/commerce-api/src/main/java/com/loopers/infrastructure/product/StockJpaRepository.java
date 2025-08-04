package com.loopers.infrastructure.product;

import com.loopers.domain.product.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductId(Long productId);

    List<Stock> findAllByProductIdIn(List<Long> productIds);
}
