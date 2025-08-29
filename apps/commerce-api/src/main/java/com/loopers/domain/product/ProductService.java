package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.loopers.support.utils.Validation.Message.MESSAGE_PRODUCT_NOT_FOUND;
import static com.loopers.support.utils.Validation.Message.MESSAGE_STOCK_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product getDetail(Long productId) {
        return productRepository.findById(productId);
    }

    public Page<Product> getList(Long brandId, Pageable pageable, ProductSortType sortType) {
        return productRepository.search(brandId, pageable, sortType);
    }

    public List<Product> getTopListByBrandId(Long brandId) {
        return productRepository.findTopListByBrandId(brandId);
    }

    public void updateLikeCount(Long productId, int likeCount) {
        productRepository.updateLikeCount(productId, likeCount);
    }

    @Transactional
    public void increaseLike(Long productId) {
        Product product = getDetail(productId);
        if (product == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_PRODUCT_NOT_FOUND);
        }
        product.increaseLike();
    }

    @Transactional
    public void decreaseLike(Long productId) {
        Product product = getDetail(productId);
        if (product == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_PRODUCT_NOT_FOUND);
        }
        product.decreaseLike();
    }

    public Stock getStockByProductId(Long productId) {
        Stock stock = productRepository.findStockByProductId(productId);
        if (stock == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_STOCK_NOT_FOUND);
        }
        return stock;
    }

    public List<Stock> getStocksByProductIds(List<Long> productIds) {
        return productRepository.findStocksByProductIds(productIds);
    }

    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Stock stock = getStockByProductId(productId);
        stock.decrease(quantity);
    }

    public Stock getStockByProductIdWithLock(Long productId) {
        Stock stock = productRepository.findStockByProductIdWithLock(productId);
        if (stock == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_STOCK_NOT_FOUND);
        }
        return stock;
    }

    @Transactional
    public void decreaseStockWithLock(Long productId, int quantity) {
        Stock stock = getStockByProductIdWithLock(productId);
        stock.decrease(quantity);
    }

    @Transactional
    public void increaseStockWithLock(Long productId, int quantity) {
        Stock stock = getStockByProductIdWithLock(productId);
        stock.increase(quantity);
    }
}
