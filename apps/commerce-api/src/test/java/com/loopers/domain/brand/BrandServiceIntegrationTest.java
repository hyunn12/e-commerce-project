package com.loopers.domain.brand;

import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BrandServiceIntegrationTest {
    // orm --
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut --
    @Autowired
    private BrandService brandService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 브랜드_조회_시 {

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 브랜드_단건_조회에_성공한다 {

            @DisplayName("존재하는 브랜드의 ID가 주어진다면")
            @Test
            void whenValidBrandId() {
                // arrange
                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드1").description("브랜드설명").build());

                // act
                Brand result = brandService.getDetail(brand.getId());

                // assert
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(brand.getId());
                assertThat(result.getName()).isEqualTo(brand.getName());
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 브랜드_목록_조회에_성공한다 {

            @DisplayName("존재하는 브랜드의 ID 목록이 주어진다면")
            @Test
            void whenValidBrandIds() {
                // arrange
                Brand brand1 = brandJpaRepository.save(Brand.builder().name("브랜드1").description("브랜드설명").build());
                Brand brand2 = brandJpaRepository.save(Brand.builder().name("브랜드2").description("브랜드설명").build());
                Brand brand3 = brandJpaRepository.save(Brand.builder().name("브랜드3").description("브랜드설명").build());
                List<Long> ids = List.of(brand1.getId(), brand3.getId());

                // act
                List<Brand> results = brandService.getListByIds(ids);

                // assert
                assertThat(results).hasSize(2)
                        .extracting(Brand::getId)
                        .containsExactlyInAnyOrder(brand1.getId(), brand3.getId());
                assertThat(results).doesNotContain(brand2);
            }
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class Null을_반환한다 {

            @DisplayName("브랜드가 존재하지 않는다면")
            @Test
            void whenBrandNotExists() {
                // act
                Brand result = brandService.getDetail(1L);

                // assert
                assertThat(result).isNull();
            }
        }
    }
}
