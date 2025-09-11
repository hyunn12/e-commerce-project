package com.loopers.application.ranking;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingInfo {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Item {
        private int rank;
        private Long productId;
        private String productName;
        private int price;
        private String brandName;
        private double score;

        public static RankingInfo.Item from(int rank, Product product, Brand brand, double score) {
            return new RankingInfo.Item(
                    rank,
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    brand.getName(),
                    score
            );
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Summary {
        private List<Item> rankings;
        private int page;
        private int size;
        private long totalCount;

        public static RankingInfo.Summary from(
                List<RankingRaw> raws,
                List<Product> products,
                List<Brand> brands,
                Pageable pageable,
                long totalCount
        ) {

            Map<Long, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));
            Map<Long, Brand> brandMap = brands.stream()
                    .collect(Collectors.toMap(Brand::getId, Function.identity()));

            List<Item> items = IntStream.range(0, raws.size())
                    .mapToObj(i -> {
                        RankingRaw raw = raws.get(i);
                        Product product = productMap.get(raw.productId());
                        if (product == null) return null;

                        Brand brand = brandMap.get(product.getBrand().getId());
                        if (brand == null) return null;

                        int rank = (int) pageable.getOffset() + i + 1;
                        return Item.from(rank, product, brand, raw.score());
                    })
                    .filter(Objects::nonNull)
                    .toList();

            return new RankingInfo.Summary(items, pageable.getPageNumber(), pageable.getPageSize(), totalCount);
        }

        public static RankingInfo.Summary empty(Pageable pageable) {
            return new RankingInfo.Summary(List.of(), pageable.getPageNumber(), pageable.getPageSize(), 0);
        }
    }
}
