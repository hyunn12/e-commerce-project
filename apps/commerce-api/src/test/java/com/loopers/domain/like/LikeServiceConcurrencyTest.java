package com.loopers.domain.like;

import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.like.LikeJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LikeServiceConcurrencyTest {
    // sut--
    @Autowired
    private LikeService likeService;

    // orm --
    @Autowired
    private LikeJpaRepository likeJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    class 좋아요_등록에_낙관적_락_적용_시 {

        @DisplayName("여러 유저가 동시에 add()를 호출해도 모든 좋아요가 저장된다.")
        @Test
        void concurrentAddMultipleUsers_AllSuccess() throws Exception {
            // arrange
            long productId   = 1L;

            int threadCount = 20;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // act
            for (int i = 1; i <= threadCount; i++) {
                final long userId = i;
                executor.submit(() -> {
                    try {
                        likeService.add(Like.of(userId, productId));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        System.out.println("[FAIL]: "+e.getMessage());
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            // assert
            System.out.println("[SUCCESS COUNT]: "+successCount.get());
            System.out.println("[FAIL COUNT]: "+failCount.get());
            assertThat(successCount.get()).isEqualTo(threadCount);

            List<Like> results = likeJpaRepository.findAll();
            assertThat(results).hasSize(threadCount);
        }
    }

    @Nested
    class 좋아요_취소에_낙관적_락_적용_시 {

        @DisplayName("여러 유저가 동시에 delete()를 호출해도, 모든 좋아요가 삭제된다")
        @Test
        void concurrentDeleteMultipleUsers_AllSuccess() throws Exception {
            // arrange
            long productId   = 1L;

            int threadCount = 20;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            for (long userId = 1; userId <= threadCount; userId++) {
                likeJpaRepository.save( Like.of(userId, productId) );
            }

            // act
            for (long userId = 1; userId <= threadCount; userId++) {
                final long uid = userId;
                executor.submit(() -> {
                    try {
                        likeService.delete(Like.of(uid, productId));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        System.out.println("[FAIL]: "+e.getMessage());
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            // assert
            System.out.println("[SUCCESS COUNT]: "+successCount.get());
            System.out.println("[FAIL COUNT]: "+failCount.get());
            assertThat(successCount.get()).isEqualTo(threadCount);

            List<Like> results = likeJpaRepository.findAll();
            assertThat(results).hasSize(threadCount);
            assertThat(results).allMatch(Like::isDeleted);
        }
    }

}
