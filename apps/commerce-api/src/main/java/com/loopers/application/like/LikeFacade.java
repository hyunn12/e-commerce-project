package com.loopers.application.like;

import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeFacade {

    private final LikeService likeService;
    private final ProductService productService;

    public LikeInfo.Main add(LikeCommand.Main command) {
        boolean isNew = likeService.add(command.toDomain());
        if (isNew) {
            productService.increaseLike(command.getProductId());
        }
        return LikeInfo.Main.from(command.getProductId(), true);
    }

    public LikeInfo.Main delete(LikeCommand.Main command) {
        boolean isNew = likeService.delete(command.toDomain());
        if (isNew) {
            productService.decreaseLike(command.getProductId());
        }
        return LikeInfo.Main.from(command.getProductId(), false);
    }
}
