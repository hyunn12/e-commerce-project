package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductCommand;
import com.loopers.application.product.ProductInfo;
import com.loopers.domain.product.ProductSortType;

import java.util.List;

public class ProductV1Dto {

    public static class ProductRequest {

        public record Summary(
                Long brandId,
                ProductSortType sort,
                int page,
                int size
        ) {
            public ProductCommand.Search toCommand() {
                return ProductCommand.Search.builder()
                        .brandId(brandId)
                        .sort(sort)
                        .page(page)
                        .size(size)
                        .build();
            }
        }
    }

    public static class ProductResponse {

        public record Detail(
                Long id,
                String name,
                int price,
                int likeCount,
                String brandName,
                String brandDesc,
                int quantity
        ) {
            public static ProductResponse.Detail from(ProductInfo.Main info) {
                return new ProductResponse.Detail(
                        info.getId(),
                        info.getName(),
                        info.getPrice(),
                        info.getLikeCount(),
                        info.getBrandName(),
                        info.getBrandDesc(),
                        info.getQuantity()
                );
            }
        }

        public record Summary(
                List<ProductInfo.Main> products,
                int page,
                int size
        ) {
            public static ProductResponse.Summary from(ProductInfo.Summary info) {
                return new ProductResponse.Summary(
                        info.getProducts(),
                        info.getPage(),
                        info.getSize()
                );
            }
        }
    }
}
