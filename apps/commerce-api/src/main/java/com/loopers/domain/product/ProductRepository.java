package com.loopers.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository {

    Product findById(Long id);

    Page<Product> search(Long brandId, Pageable pageable, ProductSortType sortType);

    Stock findStockByProductId(Long productId);
    Stock findStockByProductIdWithLock(Long productId);

    List<Stock> findStocksByProductIds(List<Long> productIds);
}
