package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional
    public void add(Like like) {
        Like exist = likeRepository.findLike(like);
        if (exist == null) {
            likeRepository.save(like);
            return;
        }
        if (exist.isDeleted()) {
            exist.restore();
        }
    }

    @Transactional
    public void delete(Like like) {
        Like exist = likeRepository.findLike(like);
        if (exist != null && !exist.isDeleted()) {
            exist.delete();
        }
    }
}
