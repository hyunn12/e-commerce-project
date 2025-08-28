package com.loopers.domain.like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductLikeCount {
    private Long productId;
    private Long likeCount;
}
