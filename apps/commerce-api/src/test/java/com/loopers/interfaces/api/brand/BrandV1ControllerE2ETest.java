package com.loopers.interfaces.api.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.infrastructure.brand.BrandJpaRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrandV1ControllerE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final BrandJpaRepository brandJpaRepository;

    @Autowired
    public BrandV1ControllerE2ETest(
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp,
            BrandJpaRepository brandJpaRepository
    ) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.brandJpaRepository = brandJpaRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("GET /api/v1/brands/{brandId}")
    @Nested
    class GET {
        private final String requestUrl = "/api/v1/brands/{brandId}";
        private Long brandId;

        @BeforeEach
        void setData() {
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("브랜드설명").build());
            brandId = brand.getId();
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 조회에_성공한_후_브랜드_정보를_받는다 {

            @DisplayName("주어진 brandId의 브랜드가 존재하는 브랜드라면")
            @Test
            void returnBrandInfo_whenValidBrandId() {
                // act
                ParameterizedTypeReference<ApiResponse<BrandV1Dto.BrandResponse>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<BrandV1Dto.BrandResponse>> response =
                        testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType, brandId);

                // assert
                assertAll(
                        () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                        () -> assertThat(response.getBody().data().id()).isEqualTo(brandId)
                );
            }
        }
    }

    @DisplayName("POST /api/v1/brands")
    @Nested
    class POST {
        private final String requestUrl = "/api/v1/brands";
        private Long brandId;

        @BeforeEach
        void setData() {
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("브랜드설명").build());
            brandId = brand.getId();
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 수정에_성공한_후_브랜드_정보를_받는다 {

            @DisplayName("주어진 brandId의 브랜드가 존재하는 브랜드라면")
            @Test
            void returnBrandInfo_whenValidBrandId() {
                // arrange
                String modifyName = "변경 브랜드명";
                String modifyDesc = "변경 브랜드설명";
                BrandV1Dto.BrandRequest requestBody = new BrandV1Dto.BrandRequest(brandId, modifyName, modifyDesc);

                // act
                ParameterizedTypeReference<ApiResponse<BrandV1Dto.BrandResponse>> responseType = new ParameterizedTypeReference<>() {};
                ResponseEntity<ApiResponse<BrandV1Dto.BrandResponse>> response =
                        testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(requestBody), responseType);

                // assert
                assertAll(
                        () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                        () -> assertThat(response.getBody().data().id()).isEqualTo(brandId),
                        () -> assertThat(response.getBody().data().name()).isEqualTo(modifyName),
                        () -> assertThat(response.getBody().data().description()).isEqualTo(modifyDesc)
                );
            }
        }
    }
}
