package com.loopers.application.like;

import com.loopers.domain.event.LikeEventPublisher;
import com.loopers.domain.event.dto.LikeAddEvent;
import com.loopers.domain.event.dto.LikeDeleteEvent;
import com.loopers.domain.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeFacade {

    private final LikeService likeService;
    private final LikeEventPublisher likeEventPublisher;

    public LikeInfo.Main add(LikeCommand.Main command) {
        boolean isNew = likeService.add(command.toDomain());
        if (isNew) {
            likeEventPublisher.publish(LikeAddEvent.of(command.getProductId()));
        }
        return LikeInfo.Main.from(command.getProductId(), true);
    }

    public LikeInfo.Main delete(LikeCommand.Main command) {
        boolean isNew = likeService.delete(command.toDomain());
        if (isNew) {
            likeEventPublisher.publish(LikeDeleteEvent.of(command.getProductId()));
        }
        return LikeInfo.Main.from(command.getProductId(), false);
    }
}
