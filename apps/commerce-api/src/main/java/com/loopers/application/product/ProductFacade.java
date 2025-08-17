package com.loopers.application.product;

import com.loopers.application.brand.BrandCacheService;
import com.loopers.application.brand.BrandInfo;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.product.Stock;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.loopers.config.redis.CacheConstants.PRODUCT_CACHE_LIMIT;
import static com.loopers.support.utils.Validation.Message.*;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final BrandService brandService;
    private final ProductService productService;
    private final ProductCacheService productCacheService;
    private final BrandCacheService brandCacheService;

    @Transactional(readOnly = true)
    public ProductInfo.Summary getList(ProductCommand.Search command) {

        Pageable pageable = command.toPageable();

        boolean isBrandIdExists = command.getBrandId() != null;
        boolean isLatestSort = command.getSort() == ProductSortType.LATEST;
        boolean isCachedRange = pageable.getOffset() < PRODUCT_CACHE_LIMIT;

        if (isBrandIdExists && isLatestSort && isCachedRange) {
            BrandInfo brandInfo = brandCacheService.getCachedBrand(command.getBrandId());

            List<ProductInfo.Main> productInfos = productCacheService.getCachedProducts(brandInfo);

            return ProductInfo.Summary.from(productInfos, pageable);
        }

        // DB 조회
        return getFromDb(command, pageable);
    }

    private ProductInfo.Summary getFromDb(ProductCommand.Search command, Pageable pageable) {
        Page<Product> products = productService.getList(command.getBrandId(), pageable, command.getSort());
        if (products.isEmpty()) {
            return ProductInfo.Summary.empty();
        }

        List<Long> brandIds = products.stream().map(product -> product.getBrand().getId()).distinct().toList();
        List<Brand> brands = brandService.getListByIds(brandIds);

        return ProductInfo.Summary.from(products, brands);
    }

    public ProductInfo.Main getDetail(Long productId) {
        Product product = productService.getDetail(productId);
        if (product == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_PRODUCT_NOT_FOUND);
        }

        Brand brand = brandService.getDetail(product.getBrand().getId());
        if (brand == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_BRAND_NOT_FOUND);
        }

        Stock stock = productService.getStockByProductId(product.getId());
        if (stock == null) {
            throw new CoreException(ErrorType.NOT_FOUND, MESSAGE_STOCK_NOT_FOUND);
        }

        return ProductInfo.Main.from(product, brand, stock);
    }
}
