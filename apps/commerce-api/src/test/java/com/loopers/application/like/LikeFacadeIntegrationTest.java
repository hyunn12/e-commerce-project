package com.loopers.application.like;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.like.Like;
import com.loopers.domain.product.Product;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.like.LikeJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LikeFacadeIntegrationTest {
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
    private LikeFacade likeFacade;

    private final Long userId = 1L;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 좋아요_등록_시 {

        @DisplayName("새로 좋아요를 등록한다면, 좋아요 등록에 성공한다.")
        @Test
        void saveLike_increaseLikeCount_whenLikeIsNew() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            Product product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품").price(10000).build());

            LikeCommand.Main command = LikeCommand.Main.builder().userId(userId).productId(product.getId()).build();

            // act
            LikeInfo.Main result = likeFacade.add(command);

            // assert
            assertThat(result.isLiked()).isTrue();
            assertThat(likeJpaRepository.findByUserIdAndProductId(userId, product.getId())).isPresent();
        }

        @DisplayName("기존 삭제된 좋아요가 있다면, 복원 후 저장에 성공한다.")
        @Test
        void restoreSuccess_whenDeletedLikeExists() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            Product product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품").price(10000).build());
            Like like = likeJpaRepository.save(Like.of(userId, product.getId()));
            like.delete();
            likeJpaRepository.save(like);

            LikeCommand.Main command = LikeCommand.Main.builder().userId(userId).productId(product.getId()).build();

            // act
            LikeInfo.Main result = likeFacade.add(command);

            // assert
            assertThat(result.isLiked()).isTrue();
            Like restoredLike = likeJpaRepository.findByUserIdAndProductId(userId, product.getId()).orElseThrow();
            assertThat(restoredLike.isDeleted()).isFalse();
        }

        @DisplayName("이미 좋아요가 있다면, 아무 동작도 하지 않는다.")
        @Test
        void doNothing_whenLikeExists() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            Product product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품").price(10000).build());
            product.increaseLike();
            productJpaRepository.save(product);
            likeJpaRepository.save(Like.of(userId, product.getId()));

            LikeCommand.Main command = LikeCommand.Main.builder().userId(userId).productId(product.getId()).build();

            // act
            LikeInfo.Main result = likeFacade.add(command);

            // assert
            assertThat(result.isLiked()).isTrue();
            assertThat(likeJpaRepository.findByUserIdAndProductId(userId, product.getId())).isPresent();
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 좋아요_취소_시 {

        @DisplayName("like 객체에 deletedAt 이 설정된다.")
        @Test
        void decreaseLikeCount_whenLikeIsCancelled() {
            // arrange
            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            Product product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품").price(10000).build());
            likeJpaRepository.save(Like.of(userId, product.getId()));
            product.increaseLike();

            LikeCommand.Main command = LikeCommand.Main.builder().userId(userId).productId(product.getId()).build();

            // act
            LikeInfo.Main result = likeFacade.delete(command);

            // assert
            assertThat(result.isLiked()).isFalse();
            Like deletedLike = likeJpaRepository.findByUserIdAndProductId(userId, product.getId()).orElseThrow();
            assertThat(deletedLike.getDeletedAt()).isNotNull();
        }
    }
}
