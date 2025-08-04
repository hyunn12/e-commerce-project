package com.loopers.application.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Search {
        private Long brandId;
        private ProductSortType sort = ProductSortType.LATEST;
        private int page = 0;
        private int size = 20;

        public Pageable toPageable() {
            return switch (sort) {
                case LATEST -> PageRequest.of(page, size, Sort.by("createdAt").descending());
                case PRICE_ASC -> PageRequest.of(page, size, Sort.by("price").ascending());
                case LIKES_DESC -> PageRequest.of(page, size, Sort.by("likeCount").descending());
            };
        }
    }
}

