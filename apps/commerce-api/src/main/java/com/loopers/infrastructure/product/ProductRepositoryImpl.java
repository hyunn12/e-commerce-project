package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.product.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryDslRepository productQueryDslRepository;
    private final StockJpaRepository stockJpaRepository;

    @Override
    public Product findById(Long id) {
        return productJpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> findAllByIds(List<Long> ids) {
        return productJpaRepository.findAllById(ids);
    }

    @Override
    public Page<Product> search(Long brandId, Pageable pageable, ProductSortType sortType) {
        return productQueryDslRepository.search(brandId, pageable, sortType);
    }

    @Override
    public List<Product> findTopListByBrandId(Long brandId) {
        return productQueryDslRepository.findTopListByBrandId(brandId);
    }

    @Override
    public void updateLikeCount(Long productId, int likeCount) {
        productJpaRepository.updateLikeCount(productId, likeCount);
    }

    @Override
    public Stock findStockByProductId(Long productId) {
        return stockJpaRepository.findByProductId(productId).orElse(null);
    }

    @Override
    public Stock findStockByProductIdWithLock(Long productId) {
        return stockJpaRepository.findByProductIdWithLock(productId).orElse(null);
    }

    @Override
    public List<Stock> findStocksByProductIds(List<Long> productIds) {
        return stockJpaRepository.findAllByProductIdIn(productIds);
    }
}
