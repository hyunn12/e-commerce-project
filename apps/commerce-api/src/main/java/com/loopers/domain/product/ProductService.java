package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.loopers.support.utils.Validation.Message.MESSAGE_PRODUCT_NOT_FOUND;
import static com.loopers.support.utils.Validation.Message.MESSAGE_STOCK_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public Product getDetail(Long productId) {
        return productRepository.findById(productId);
    }

    public Page<Product> getList(Pageable pageable) {
        return productRepository.findAll(pageable);
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

    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Stock stock = stockRepository.findByProductId(productId);
        if (stock == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_STOCK_NOT_FOUND);
        }
        stock.decrease(quantity);
    }
}
