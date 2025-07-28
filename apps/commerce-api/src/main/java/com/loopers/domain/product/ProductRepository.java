package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository {

    Product findById(Long id);

    Page<Product> findAllByBrand(Brand brand, Pageable pageable);

}
