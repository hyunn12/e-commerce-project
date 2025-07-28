package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getProductsByBrand(Brand brand, Pageable pageable) {
        return productRepository.findAllByBrand(brand, pageable);
    }

}
