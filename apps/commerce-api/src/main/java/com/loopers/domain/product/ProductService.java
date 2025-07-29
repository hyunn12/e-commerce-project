package com.loopers.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product getDetail(Long productId) {
        return productRepository.findById(productId);
    }

    public Page<Product> getList(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}
