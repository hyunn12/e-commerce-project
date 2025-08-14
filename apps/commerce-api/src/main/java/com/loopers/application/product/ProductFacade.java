package com.loopers.application.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.product.Stock;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.loopers.support.utils.Validation.Message.*;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final BrandService brandService;
    private final ProductService productService;

    public ProductInfo.Summary getList(ProductCommand.Search command) {
        Page<Product> products = productService.getList(command.getBrandId(), command.toPageable(), command.getSort());
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
