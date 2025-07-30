package com.loopers.application.like;

import com.loopers.domain.like.Like;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeCommand {

    @Getter
    @Builder
    public static class Main {
        private Long userId;
        private Long productId;

        public Like toDomain() {
            return Like.of(userId, productId);
        }
    }
}
