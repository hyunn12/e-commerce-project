package com.loopers.domain.brand;

import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        class Not_Fount_예외가_발생한다 {

            @DisplayName("브랜드가 존재하지 않는다면")
            @Test
            void whenBrandNotExists() {
                // act
                CoreException exception = assertThrows(CoreException.class, () -> brandService.getDetail(1L));

                // assert
                assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            }
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 브랜드_정보_수정_시 {

        @DisplayName("존재하는 브랜드의 ID가 주어진다면 브랜드 정보 수정에 성공한다.")
        @Test
        void successModify_whenValidBrandId() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드1").description("브랜드설명").build());
            String modifyName = "변경 브랜드명";
            String modifyDesc = "변경 브랜드설명";

            // act
            Brand result = brandService.modify(brand.getId(), modifyName, modifyDesc);

            // assert
            assertThat(result.getId()).isEqualTo(brand.getId());
            assertThat(result.getName()).isEqualTo(modifyName);
            assertThat(result.getDescription()).isEqualTo(modifyDesc);
        }

        @DisplayName("존재하지않는 브랜드의 ID가 주어진다면 404 Not Found 예외가 발생한다.")
        @Test
        void throwsNotFoundException_whenInValidBrandId() {
            // arrange
            String modifyName = "변경 브랜드명";
            String modifyDesc = "변경 브랜드설명";

            // act
            CoreException exception = assertThrows(CoreException.class, () -> brandService.modify(1L, modifyName, modifyDesc));

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
