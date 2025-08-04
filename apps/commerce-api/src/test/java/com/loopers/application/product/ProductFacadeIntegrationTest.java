package com.loopers.application.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.Stock;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.product.StockJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.stream.Collectors;

import static com.loopers.domain.product.ProductSortType.LATEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ProductFacadeIntegrationTest {
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
    private ProductFacade productFacade;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 상품_목록_조회_시 {

        @DisplayName("상품 목록이 존재하면 브랜드와 재고 포함한 목록 정보가 반환된다.")
        @Test
        void returnProductInfo_whenProductsExist() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드1").description("설명").build());
            Product product1 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품1").price(10000).build());
            Product product2 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품2").price(20000).build());
            Stock stock1 = stockJpaRepository.save(Stock.builder().product(product1).quantity(5).build());
            Stock stock2 = stockJpaRepository.save(Stock.builder().product(product2).quantity(10).build());

            ProductCommand.Search command = new ProductCommand.Search(null, LATEST, 0, 10);

            // act
            ProductInfo.Summary result = productFacade.getList(command);

            // assert
            assertThat(result.getProducts()).hasSize(2);
            assertThat(result.getProducts())
                    .extracting(ProductInfo.Main::getBrandName)
                    .containsOnly(brand.getName());
            assertThat(result.getProducts())
                    .extracting(ProductInfo.Main::getQuantity)
                    .containsExactlyInAnyOrder(stock1.getQuantity(), stock2.getQuantity());
        }

        @DisplayName("상품이 없으면 빈 목록을 반환한다.")
        @Test
        void returnEmptyList_whenProductNotExist() {
            // arrange
            ProductCommand.Search command = new ProductCommand.Search(null, LATEST, 0, 10);

            // act
            ProductInfo.Summary result = productFacade.getList(command);

            // assert
            assertThat(result.getProducts()).isEmpty();
        }

        @DisplayName("서로 다른 브랜드의 상품이 주어진다면 각 상품에 브랜드 정보가 정확히 매핑된다.")
        @Test
        void mapProductBrand_whenProductsHaveDifferentBrands() {
            // arrange
            Brand brand1 = brandJpaRepository.save(Brand.builder().name("브랜드1").description("설명").build());
            Brand brand2 = brandJpaRepository.save(Brand.builder().name("브랜드2").description("설명").build());
            Product product1 = productJpaRepository.save(Product.createBuilder().brand(brand1).name("상품1").price(10000).build());
            Product product2 = productJpaRepository.save(Product.createBuilder().brand(brand2).name("상품2").price(20000).build());
            stockJpaRepository.save(Stock.builder().product(product1).quantity(3).build());
            stockJpaRepository.save(Stock.builder().product(product2).quantity(7).build());

            ProductCommand.Search command = new ProductCommand.Search(null, LATEST, 0, 10);

            // act
            ProductInfo.Summary result = productFacade.getList(command);

            // assert
            Map<String, String> nameToBrandMap = result.getProducts().stream()
                    .collect(Collectors.toMap(ProductInfo.Main::getName, ProductInfo.Main::getBrandName));
            assertThat(nameToBrandMap)
                    .containsEntry(product1.getName(), brand1.getName())
                    .containsEntry(product2.getName(), brand2.getName());
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 상품_단건_조회_시 {

        @DisplayName("상품이 존재하면 브랜드와 재고 포함한 정보가 반환된다.")
        @Test
        void returnProductInfo_whenProductExists() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드1").description("설명").build());
            Product product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품1").price(10000).build());
            Stock stock = stockJpaRepository.save(Stock.builder().product(product).quantity(5).build());

            // act
            ProductInfo.Main result = productFacade.getDetail(product.getId());

            // assert
            assertThat(result)
                    .extracting(ProductInfo.Main::getBrandName, ProductInfo.Main::getQuantity)
                    .containsExactlyInAnyOrder(brand.getName(), stock.getQuantity());
        }

        @DisplayName("상품이 존재하지 않는다면 404 Not Found 예외가 발생한다.")
        @Test
        void throwNotFoundException_whenProductNotExist() {
            // act
            CoreException exception = assertThrows(CoreException.class, () ->
                    productFacade.getDetail(1L)
            );

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
