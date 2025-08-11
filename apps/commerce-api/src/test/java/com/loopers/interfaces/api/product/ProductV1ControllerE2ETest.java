package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductInfo;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.Stock;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.product.StockJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static com.loopers.domain.product.ProductSortType.LATEST;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductV1ControllerE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final BrandJpaRepository brandJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final StockJpaRepository stockJpaRepository;

    @Autowired
    public ProductV1ControllerE2ETest(
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp,
            BrandJpaRepository brandJpaRepository,
            ProductJpaRepository productJpaRepository,
            StockJpaRepository stockJpaRepository
    ) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.brandJpaRepository = brandJpaRepository;
        this.productJpaRepository = productJpaRepository;
        this.stockJpaRepository = stockJpaRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("GET /api/v1/products")
    @Nested
    class GET_getList {
        private final String requestUrl = "/api/v1/products";
        private Long brandId;
        private Long productId1;
        private Long productId2;

        @BeforeEach
        void setData() {
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("브랜드설명").build());
            Product product1 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품1").price(10000).build());
            Product product2 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품2").price(20000).build());
            stockJpaRepository.save(Stock.builder().product(product1).quantity(10).build());
            stockJpaRepository.save(Stock.builder().product(product2).quantity(20).build());

            brandId = brand.getId();
            productId1 = product1.getId();
            productId2 = product2.getId();
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 조회에_성공한_후_상품_목록을_받는다 {

            @DisplayName("주어진 조건의 상품 목록이 존재하면")
            @Test
            void returnProductInfo_whenProductsExist() {
                // arrange
                String url = UriComponentsBuilder.fromPath(requestUrl)
                        .queryParam("brandId", brandId)
                        .queryParam("sort", LATEST)
                        .queryParam("page", 0)
                        .queryParam("size", 20)
                        .build()
                        .toUriString();

                // act
                ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse.Summary>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<ProductV1Dto.ProductResponse.Summary>> response =
                        testRestTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, responseType);

                // assert
                assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                System.out.println("body=" + (response.getBody() == null ? "null" : response.getBody()));
                System.out.println("data=" + (response.getBody().data() == null ? "null" : response.getBody().data()));
                System.out.println("products=" + (response.getBody().data() == null ? "null" : response.getBody().data().products()));
                assertThat(response.getBody().data().products()).hasSize(2);
                List<Long> ids = response.getBody().data().products().stream().map(ProductInfo.Main::getId).collect(Collectors.toList());
                assertThat(ids).containsExactlyInAnyOrder(productId1, productId2);
            }
        }
    }

    @DisplayName("GET /api/v1/products/{productId}")
    @Nested
    class GET_getDetail {
        private final String requestUrl = "/api/v1/products/{productId}";
        private Brand brand;
        private Product product;
        private Stock stock;

        @BeforeEach
        void setData() {
            brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("브랜드설명").build());
            product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품1").price(10000).build());
            stock = stockJpaRepository.save(Stock.builder().product(product).quantity(10).build());
        }

        @DisplayName("주어진 productId의 상품이 존재하면")
        @Test
        void returnProductDetail_whenValidProductId() {
            ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse.Detail>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<ProductV1Dto.ProductResponse.Detail>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity.EMPTY, responseType, product.getId());

            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().id()).isEqualTo(product.getId());
            assertThat(response.getBody().data().name()).isEqualTo(product.getName());
            assertThat(response.getBody().data().price()).isEqualTo(product.getPrice());
            assertThat(response.getBody().data().brandName()).isEqualTo(brand.getName());
            assertThat(response.getBody().data().quantity()).isEqualTo(stock.getQuantity());
        }
    }
}
