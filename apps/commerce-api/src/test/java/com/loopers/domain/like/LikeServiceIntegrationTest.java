package com.loopers.domain.like;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.like.LikeJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LikeServiceIntegrationTest {
    // orm --
    @Autowired
    private LikeJpaRepository likeJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private BrandJpaRepository brandJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    // sut --
    @Autowired
    private LikeService likeService;

    private final Long userId = 1L;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 좋아요_등록_시 {

        @DisplayName("새로 좋아요를 등록한다면 좋아요 등록에 성공한다.")
        @Test
        void saveSuccess_whenLikeIsNew() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            Product product = productJpaRepository.save(Product.builder().brand(brand).name("상품").price(10000).build());
            Like like = Like.of(userId, product.getId());

            // act
            likeService.add(like);

            // assert
            List<Like> likes = likeJpaRepository.findAll();
            assertThat(likes).hasSize(1);
            assertThat(likes.get(0).getUserId()).isEqualTo(userId);
        }

        @DisplayName("기존 삭제된 좋아요가 있다면 복원 후 저장한다.")
        @Test
        void restoreSuccess_whenDeletedLikeExists() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            Product product = productJpaRepository.save(Product.builder().brand(brand).name("상품").price(10000).build());
            Like like = likeJpaRepository.save(Like.of(userId, product.getId()));
            like.delete();
            likeJpaRepository.save(like);

            // act
            likeService.add(like);

            // assert
            Like result = likeJpaRepository.findByUserIdAndProductId(userId, product.getId()).get();
            assertThat(result.getDeletedAt()).isNull();
        }

        @DisplayName("이미 좋아요가 있다면 아무 동작도 하지 않는다.")
        @Test
        void doNothing_whenLikeExists() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            Product product = productJpaRepository.save(Product.builder().brand(brand).name("상품").price(10000).build());
            Like like = likeJpaRepository.save(Like.of(userId, product.getId()));

            // act
            likeService.add(like);

            // assert
            List<Like> likes = likeJpaRepository.findAll();
            assertThat(likes).hasSize(1);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 좋아요_취소_시 {

        @DisplayName("좋아요를 취소한다면 deletedAt 이 설정된다.")
        @Test
        void setDeletedAt_whenLikeIsCancelled() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            Product product = productJpaRepository.save(Product.builder().brand(brand).name("상품").price(10000).build());
            Like like = likeJpaRepository.save(Like.of(userId, product.getId()));

            // act
            likeService.delete(like);

            // assert
            Like result = likeJpaRepository.findByUserIdAndProductId(userId, product.getId()).get();
            assertThat(result.getDeletedAt()).isNotNull();
        }
    }
}
