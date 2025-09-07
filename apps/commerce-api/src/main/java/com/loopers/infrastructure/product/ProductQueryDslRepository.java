package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.product.QProduct;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.loopers.redis.config.CacheConstants.PRODUCT_CACHE_LIMIT;

@Component
@RequiredArgsConstructor
public class ProductQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Product> search(Long brandId, Pageable pageable, ProductSortType sortType) {
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        if (brandId != null) {
            builder.and(product.brand.id.eq(brandId));
        }
        builder.and(product.deletedAt.isNull());

        OrderSpecifier<?> orderSpecifier = switch (sortType) {
            case LATEST -> product.createdAt.desc();
            case PRICE_ASC -> product.price.asc();
            case LIKES_DESC -> product.likeCount.desc();
        };

        List<Product> products = jpaQueryFactory
                .selectFrom(product)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(products, pageable, total != null ? total : 0);
    }

    public List<Product> findTopListByBrandId(Long brandId) {
        QProduct product = QProduct.product;

        BooleanBuilder builder = new BooleanBuilder()
                .and(product.brand.id.eq(brandId));

        return jpaQueryFactory.selectFrom(product)
                .where(builder)
                .orderBy(product.createdAt.desc())
                .limit(PRODUCT_CACHE_LIMIT)
                .fetch();
    }
}
