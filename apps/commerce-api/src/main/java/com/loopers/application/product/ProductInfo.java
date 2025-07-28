package com.loopers.application.product;

import com.loopers.application.brand.BrandInfo;
import com.loopers.domain.product.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInfo {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Main {
        private Long id;
        private String name;
        private int price;
        private BrandInfo brand;

        public static ProductInfo.Main from(Product product) {
            return new ProductInfo.Main(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    BrandInfo.from(product.getBrand())
            );
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Search {
        private List<Main> products;
        private int page;
        private int size;

        public static Search from(Page<Product> productPage) {
            List<Main> products = productPage.getContent().stream()
                    .map(product -> Main.from(product))
                    .toList();

            return new Search(
                    products,
                    productPage.getNumber(),
                    productPage.getSize()
            );
        }
    }
}

