package com.loopers.application.product;

import com.loopers.application.brand.BrandInfo;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.loopers.config.redis.CacheConstants.*;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final ProductService productService;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<ProductInfo.Main> getCachedProducts(BrandInfo brandInfo) {

        String key = PRODUCTS_CACHE_KEY_PREFIX + brandInfo.getId() + PRODUCTS_CACHE_KEY_SUFFIX;

        // 캐시 조회
        ProductInfoList cachedProducts = (ProductInfoList) redisTemplate.opsForValue().get(key);
        if (cachedProducts != null) {
            return cachedProducts.getMains();
        }

        // DB 조회
        List<Product> products = productService.getTopListByBrandId(brandInfo.getId());

        List<ProductInfo.Main> productInfos = products.stream()
                .map(p -> ProductInfo.Main.from(p, brandInfo))
                .toList();
        ProductInfoList list = ProductInfoList.of(productInfos);

        // 캐시 저장
        redisTemplate.opsForValue().set(key, list, PRODUCTS_CACHE_TTL);

        return productInfos;
    }
}
