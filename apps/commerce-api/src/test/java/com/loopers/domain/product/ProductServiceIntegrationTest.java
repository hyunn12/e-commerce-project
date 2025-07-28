package com.loopers.domain.product;

import com.loopers.application.product.ProductCommand;
import com.loopers.application.product.ProductSortType;
import com.loopers.domain.brand.Brand;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceIntegrationTest {
    // orm --
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut --
    @Autowired
    private ProductService productService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 상품_목록_조회_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 정상적으로_상품_목록이_조회된다 {

            @DisplayName("정상적인 파라미터가 주어진 경우")
            @Test
            void success() {
                // arrange
                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드1").description("브랜드설명").build());
                Product product1 = productJpaRepository.save(Product.builder().brand(brand).name("상품1").price(10000).build());
                Product product2 = productJpaRepository.save(Product.builder().brand(brand).name("상품2").price(50000).build());

                Pageable pageable = PageRequest.of(0, 10);

                // act
                Page<Product> result = productService.getProductsByBrand(brand, pageable);

                // assert
                assertThat(result).hasSize(2);
                assertThat(result.getContent())
                        .extracting(Product::getName)
                        .containsExactlyInAnyOrder(product1.getName(), product2.getName());
            }

            @DisplayName("최신순 정렬로 조회한 경우")
            @Test
            void whenSortIsLatest() throws InterruptedException {
                // arrange
                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드1").description("브랜드설명").build());
                Product product1 = productJpaRepository.save(Product.builder().brand(brand).name("상품1").price(10000).build());
                Thread.sleep(10);
                Product product2 = productJpaRepository.save(Product.builder().brand(brand).name("상품2").price(50000).build());

                ProductCommand.Search search = new ProductCommand.Search(brand.getId(), ProductSortType.LATEST, 0, 10);

                // act
                Page<Product> result = productService.getProductsByBrand(brand, search.toPageable());

                // assert
                assertThat(result.getContent())
                        .extracting(Product::getName)
                        .containsExactly(product2.getName(), product1.getName());
            }

            @DisplayName("가격 오름차순 정렬로 조회한 경우")
            @Test
            void whenSortIsPriceAsc() {
                // arrange
                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드1").description("설명").build());
                Product product1 = productJpaRepository.save(Product.builder().brand(brand).name("상품1").price(50000).build());
                Product product2 = productJpaRepository.save(Product.builder().brand(brand).name("상품2").price(10000).build());
                Product product3 = productJpaRepository.save(Product.builder().brand(brand).name("상품3").price(30000).build());

                ProductCommand.Search search = new ProductCommand.Search(brand.getId(), ProductSortType.PRICE_ASC, 0, 10);

                // act
                Page<Product> result = productService.getProductsByBrand(brand, search.toPageable());

                // assert
                assertThat(result.getContent())
                        .extracting(Product::getPrice)
                        .containsExactly(product2.getPrice(), product3.getPrice(), product1.getPrice());
                assertThat(result.getContent())
                        .extracting(Product::getName)
                        .containsExactly(product2.getName(), product3.getName(), product1.getName());
            }

        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 빈_목록이_조회된다 {
            @DisplayName("페이지 크기보다 작은 범위를 요청한 경우")
            @Test
            void whenPageIsOutOfBounds() {
                // arrange
                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드1").description("브랜드설명").build());
                productJpaRepository.save(Product.builder().brand(brand).name("상품1").price(10000).build());

                Pageable pageable = PageRequest.of(1, 10);

                // act
                Page<Product> result = productService.getProductsByBrand(brand, pageable);

                // assert
                assertThat(result.getContent()).isEmpty();
            }

            @DisplayName("상품이 없는 브랜드인 경우")
            @Test
            void whenBrandHasNoProducts() {
                // arrange
                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드1").description("브랜드설명").build());

                Pageable pageable = PageRequest.of(0, 10);

                // act
                Page<Product> result = productService.getProductsByBrand(brand, pageable);

                // assert
                assertThat(result.getContent()).isEmpty();
            }
        }
    }
}
