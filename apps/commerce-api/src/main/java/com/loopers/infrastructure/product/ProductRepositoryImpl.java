package com.loopers.infrastructure.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Product findById(Long id) {
        return productJpaRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Product> findAllByBrand(Brand brand, Pageable pageable) {
        return productJpaRepository.findAllByBrand(brand, pageable);
    }
}
