package com.loopers.infrastructure.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.QBrand;
import com.loopers.domain.product.QProduct;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.loopers.config.redis.CacheConstants.BRAND_CACHE_LIMIT;

@Component
@RequiredArgsConstructor
public class BrandQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Brand> findTopList() {
        QBrand brand = QBrand.brand;
        QProduct product = QProduct.product;

        BooleanBuilder builder = new BooleanBuilder()
                .and(brand.deletedAt.isNull())
                .and(product.deletedAt.isNull());

        return jpaQueryFactory
                .select(product.brand)
                .from(product)
                .join(product.brand, brand)
                .where(builder)
                .groupBy(product.brand)
                .orderBy(product.count().desc())
                .limit(BRAND_CACHE_LIMIT)
                .fetch();
    }
}
