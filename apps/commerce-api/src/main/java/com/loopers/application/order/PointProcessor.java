package com.loopers.application.order;


import com.loopers.domain.point.PointService;
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
public class PointProcessor {

    private final PointService pointService;

    @Retryable(
            retryFor = {OptimisticLockException.class, StaleObjectStateException.class, ObjectOptimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 50)
    )
    @Transactional
    public void use(Long userId, int amount, Long orderId) {
        pointService.use(userId, amount, orderId);
    }

    @Transactional
    public void useWithLock(Long userId, int amount, Long orderId) {
        pointService.useWithLock(userId, amount, orderId);
    }

    @Transactional
    public void restoreWithLock(Long userId, int amount, Long orderId) {
        pointService.restoreWithLock(userId, amount, orderId);
    }
}
