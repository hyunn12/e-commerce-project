package com.loopers.domain.like;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    @Retryable(
            retryFor = {OptimisticLockException.class, StaleObjectStateException.class, ObjectOptimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 50)
    )
    @Transactional
    public boolean add(Like like) {
        Like exist = likeRepository.findLike(like);
        if (exist == null) {
            likeRepository.save(like);
            return true;
        }
        if (exist.isDeleted()) {
            exist.restore();
            return true;
        }
        return false;
    }

    @Retryable(
            retryFor = {OptimisticLockException.class, StaleObjectStateException.class, ObjectOptimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 50)
    )
    @Transactional
    public boolean delete(Like like) {
        Like exist = likeRepository.findLike(like);
        if (exist != null && !exist.isDeleted()) {
            exist.delete();
            return true;
        }
        return false;
    }
}
