package com.loopers.application.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.Stock;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInfo {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Main {
        private Long id;
        private String name;
        private int price;
        private int likeCount;
        private String brandName;
        private String brandDesc;
        private int quantity;

        public static ProductInfo.Main from(Product product, Brand brand, Stock stock) {
            return new ProductInfo.Main(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getLikeCount(),
                    brand.getName(),
                    brand.getDescription(),
                    stock != null ? stock.getQuantity() : 0
            );
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Search {
        private List<Main> products;
        private int page;
        private int size;

        public static Search from(Page<Product> productPage, List<Brand> brands, List<Stock> stocks) {
            Map<Long, Brand> brandMap = brands.stream()
                .collect(Collectors.toMap(Brand::getId, Function.identity()));

            Map<Long, Stock> stockMap = stocks.stream()
                    .collect(Collectors.toMap(stock -> stock.getProduct().getId(), Function.identity()));

            List<Main> products = productPage.getContent().stream()
                    .map(product -> Main.from(
                            product,
                            brandMap.get(product.getBrand().getId()),
                            stockMap.get(product.getId())
                    ))
                    .toList();

            return new Search(products, productPage.getNumber(), productPage.getSize());
        }

        public static Search empty() {
            return new Search(Collections.emptyList(), 0, 0);
        }
    }
}

