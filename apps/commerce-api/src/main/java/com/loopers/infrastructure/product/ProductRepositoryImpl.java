package com.loopers.infrastructure.product;

import com.loopers.domain.product.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final StockJpaRepository stockJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Product findById(Long id) {
        return productJpaRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Product> search(Long brandId, Pageable pageable, ProductSortType sortType) {
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        if (brandId != null) {
            builder.and(product.brand.id.eq(brandId));
        }

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
