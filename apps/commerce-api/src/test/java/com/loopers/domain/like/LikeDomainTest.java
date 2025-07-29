package com.loopers.domain.like;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class LikeDomainTest {

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 좋아요_객체_생성_시 {

        @DisplayName("userId와 productId가 주어지면 정상적으로 객체를 생성할 수 있다.")
        @Test
        void returnLike_whenUserIdAndProductIdIsGiven() {
            // arrange
            Long userId = 1L;
            Long productId = 1L;

            // act
            Like like = Like.of(userId, productId);

            // assert
            assertThat(like.getUserId()).isEqualTo(userId);
            assertThat(like.getProductId()).isEqualTo(productId);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 좋아요_삭제_시 {

        @DisplayName("delete() 호출하면 deletedAt이 설정된다.")
        @Test
        void returnDeletedAtIsNotNull() {
            // arrange
            Like like = Like.of(1L, 1L);

            // act
            like.delete();

            // assert
            assertThat(like.isDeleted()).isTrue();
            assertThat(like.getDeletedAt()).isNotNull();
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 좋아요_복원_시 {

        @DisplayName("restore() 호출하면 deletedAt이 null이 된다.")
        @Test
        void returnDeletedAtIsNull() {
            // arrange
            Like like = Like.of(1L, 100L);
            like.delete();

            // act
            like.restore();

            // assert
            assertThat(like.isDeleted()).isFalse();
            assertThat(like.getDeletedAt()).isNull();
        }
    }
}
