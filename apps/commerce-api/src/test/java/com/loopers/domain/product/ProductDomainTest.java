package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDomainTest {

    final Brand brand = Instancio.create(Brand.class);

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 좋아요_증가_시 {

        @DisplayName("정상적으로 likeCount가 1 증가한다.")
        @Test
        void likeCountIsIncremented_whenIncreaseLike() {
            // arrange
            Product product = Product.createBuilder().brand(brand).name("상품").price(10000).build();

            // act
            product.increaseLike();

            // assert
            assertThat(product.getLikeCount()).isEqualTo(1);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 좋아요_감소_시 {

        @DisplayName("정상적으로 likeCount가 1 감소한다.")
        @Test
        void likeCountIsDecremented_whenDecreaseLike() {
            // arrange
            Product product = Product.createBuilder().brand(brand).name("상품").price(10000).build();
            product.increaseLike();

            // act
            product.decreaseLike();

            // assert
            assertThat(product.getLikeCount()).isEqualTo(0);
        }

        @DisplayName("likeCount가 0인 경우 감소하지 않는다.")
        @Test
        void likeCountIsNotDecremented_whenLikeCountIsZero() {
            // arrange
            Product product = Product.createBuilder().brand(brand).name("상품").price(10000).build();

            // act
            product.decreaseLike();

            // assert
            assertThat(product.getLikeCount()).isEqualTo(0);
        }
    }
}
