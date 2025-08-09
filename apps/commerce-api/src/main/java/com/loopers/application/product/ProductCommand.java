package com.loopers.application.product;

import com.loopers.domain.product.ProductSortType;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCommand {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Search {
        private Long brandId;
        @Builder.Default
        private ProductSortType sort = ProductSortType.LATEST;
        @Builder.Default
        private int page = 0;
        @Builder.Default
        private int size = 20;

        public Pageable toPageable() {
            return PageRequest.of(page, size);
        }
    }
}

