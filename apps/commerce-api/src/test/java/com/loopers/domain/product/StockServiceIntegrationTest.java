package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.product.StockJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockServiceIntegrationTest {
    // orm--
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private StockJpaRepository stockJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut --
    @Autowired
    private StockService stockService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 재고_조회_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 재고_단건_조회에_성공한다 {

            @DisplayName("존재하는 상품의 ID가 주어진다면")
            @Test
            void whenValidProductId() {
                // arrange
                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
                Product product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품").price(10000).build());
                Stock stock = stockJpaRepository.save(Stock.builder().product(product).quantity(50).build());

                // act
                Stock result = stockService.getDetailByProductId(product.getId());

                // assert
                assertThat(result).isNotNull();
                assertThat(result.getProduct().getId()).isEqualTo(product.getId());
                assertThat(result.getQuantity()).isEqualTo(stock.getQuantity());
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 재고_목록_조회에_성공한다 {

            @DisplayName("존재하는 상품들의 ID 목록이 주어진다면")
            @Test
            void whenValidProductIds() {
                // arrange
                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
                Product product1 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품1").price(10000).build());
                Product product2 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품2").price(20000).build());
                List<Long> productIds = List.of(product1.getId(), product2.getId());

                Stock stock1 = stockJpaRepository.save(Stock.builder().product(product1).quantity(10).build());
                Stock stock2 = stockJpaRepository.save(Stock.builder().product(product2).quantity(20).build());

                // act
                List<Stock> results = stockService.getListByProductIds(productIds);

                // assert
                assertThat(results).hasSize(2);
                assertThat(results)
                        .extracting(stock -> stock.getProduct().getId())
                        .containsExactlyInAnyOrder(stock1.getProduct().getId(), stock2.getProduct().getId());
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class Null을_반환한다 {

            @DisplayName("주어진 productId에 해당하는 재고가 존재하지 않는다면")
            @Test
            void whenStockNotExists() {
                // act
                Stock result = stockService.getDetailByProductId(1L);

                // assert
                assertThat(result).isNull();
            }
        }
    }
}
