package com.loopers.application.like;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeInfo {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Main {
        private Long productId;
        private boolean isLiked;

        public static LikeInfo.Main from(Long productId, boolean isLiked) {
            return new LikeInfo.Main(productId, isLiked);
        }
    }
}
