package com.loopers.application.order;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointHistory;
import com.loopers.infrastructure.point.PointHistoryJpaRepository;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("포인트 사용에 락 적용 시")
@SpringBootTest
class PointUseServiceConcurrencyTest {
    // sut --
    @Autowired
    private PointUseService pointUseService;

    // orm--
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 포인트_사용할_때_낙관적_락_적용_시 {

        @DisplayName("포인트가 충분하다면 모든 주문에 대해 포인트가 정확히 사용된다.")
        @Test
        void usePoint() throws Exception {
            // arrange
            int threadCount = 10;
            int requestCount = 10;

            int initialPoint = 50000 * requestCount;
            int usePoint = 10000;
            Point point = new Point(1L, initialPoint);
            pointJpaRepository.save(point);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(requestCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            long start = System.currentTimeMillis();

            // act
            for (int i = 0; i < requestCount; i++) {
                executor.submit(() -> {
                    try {
                        pointUseService.use(1L, usePoint);
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

            long end = System.currentTimeMillis();
            System.out.println("[소요시간]: " + (end - start) + "ms");

            // assert
            System.out.println("[SUCCESS COUNT]: "+successCount.get());
            System.out.println("[FAIL COUNT]: "+failCount.get());
        }

        @DisplayName("포인트가 부족하다면 포인트가 유효한 주문에 대해서만 포인트가 사용된다.")
        @Test
        void usePoint_whenPointIsNotEnough() throws Exception {
            // arrange
            int threadCount = 50;
            int requestCount = 1000;

            int initialPoint = 50000 * requestCount;
            int usePoint = 20000 * requestCount;
            Point point = new Point(1L, initialPoint);
            pointJpaRepository.save(point);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(requestCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            long start = System.currentTimeMillis();

            // act
            for (int i = 0; i < requestCount; i++) {
                executor.submit(() -> {
                    try {
                        pointUseService.use(1L, usePoint);
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

            long end = System.currentTimeMillis();
            System.out.println("[소요시간]: " + (end - start) + "ms");

            // assert
            System.out.println("[SUCCESS COUNT]: "+successCount.get());
            System.out.println("[FAIL COUNT]: "+failCount.get());

            Point result = pointJpaRepository.findById(point.getId()).orElseThrow();
            System.out.println("[남은 포인트]: " + result.getPoint());
            assertThat(result.getPoint()).isEqualTo(initialPoint-(2*usePoint));

            List<PointHistory> histories = pointHistoryJpaRepository.findByUserId(1L);
            System.out.println("[포인트 사용 이력 크기]: " + histories.size());
            assertThat(histories).hasSize(2);
        }

        @DisplayName("포인트가 0이라면 모든 주문에 대해 포인트 사용이 실패한다.")
        @Test
        void usePoint_whenPointIsZero() throws Exception {
            // arrange
            int threadCount = 50;
            int requestCount = 1000;

            int initialPoint = 0;
            int usePoint = 10000;
            Point point = new Point(1L, initialPoint);
            pointJpaRepository.save(point);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(requestCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            long start = System.currentTimeMillis();

            // act
            for (int i = 0; i < requestCount; i++) {
                executor.submit(() -> {
                    try {
                        pointUseService.use(1L, usePoint);
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

            long end = System.currentTimeMillis();
            System.out.println("[소요시간]: " + (end - start) + "ms");

            // assert
            System.out.println("[SUCCESS COUNT]: "+successCount.get());
            System.out.println("[FAIL COUNT]: "+failCount.get());

            Point result = pointJpaRepository.findById(point.getId()).orElseThrow();
            System.out.println("[남은 포인트]: " + result.getPoint());
            assertThat(result.getPoint()).isEqualTo(initialPoint);

            List<PointHistory> histories = pointHistoryJpaRepository.findByUserId(1L);
            System.out.println("[포인트 사용 이력 크기]: " + histories.size());
            assertThat(histories).hasSize(0);
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 포인트_사용할_때_비관적_락_적용_시 {

        @DisplayName("포인트가 충분하다면 모든 주문에 대해 포인트가 정확히 사용된다.")
        @Test
        void usePoint() throws Exception {
            // arrange
            int threadCount = 50;
            int requestCount = 1000;

            int initialPoint = 50000 * requestCount;
            int usePoint = 10000;
            Point point = new Point(1L, initialPoint);
            pointJpaRepository.save(point);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(requestCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            long start = System.currentTimeMillis();

            // act
            for (int i = 0; i < requestCount; i++) {
                executor.submit(() -> {
                    try {
                        pointUseService.useWithLock(1L, usePoint);
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

            long end = System.currentTimeMillis();
            System.out.println("[소요시간]: " + (end - start) + "ms");

            // assert
            System.out.println("[SUCCESS COUNT]: "+successCount.get());
            System.out.println("[FAIL COUNT]: "+failCount.get());

            Point result = pointJpaRepository.findById(point.getId()).orElseThrow();
            System.out.println("[남은 포인트]: " + result.getPoint());
            assertThat(result.getPoint()).isEqualTo(initialPoint-(successCount.get()*usePoint));

            List<PointHistory> histories = pointHistoryJpaRepository.findByUserId(1L);
            System.out.println("[포인트 사용 이력 크기]: " + histories.size());
            assertThat(histories).hasSize(successCount.get());
        }

        @DisplayName("포인트가 부족하다면 포인트가 유효한 주문에 대해서만 포인트가 사용된다.")
        @Test
        void usePoint_whenPointIsNotEnough() throws Exception {
            // arrange
            int threadCount = 50;
            int requestCount = 1000;

            int initialPoint = 50000 * requestCount;
            int usePoint = 20000 * requestCount;
            Point point = new Point(1L, initialPoint);
            pointJpaRepository.save(point);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(requestCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            long start = System.currentTimeMillis();

            // act
            for (int i = 0; i < requestCount; i++) {
                executor.submit(() -> {
                    try {
                        pointUseService.useWithLock(1L, usePoint);
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

            long end = System.currentTimeMillis();
            System.out.println("[소요시간]: " + (end - start) + "ms");

            // assert
            System.out.println("[SUCCESS COUNT]: "+successCount.get());
            System.out.println("[FAIL COUNT]: "+failCount.get());

            Point result = pointJpaRepository.findById(point.getId()).orElseThrow();
            System.out.println("[남은 포인트]: " + result.getPoint());
            assertThat(result.getPoint()).isEqualTo(initialPoint-(2*usePoint));

            List<PointHistory> histories = pointHistoryJpaRepository.findByUserId(1L);
            System.out.println("[포인트 사용 이력 크기]: " + histories.size());
            assertThat(histories).hasSize(2);
        }

        @DisplayName("포인트가 0이라면 모든 주문에 대해 포인트 사용이 실패한다.")
        @Test
        void usePoint_whenPointIsZero() throws Exception {
            // arrange
            int threadCount = 50;
            int requestCount = 1000;

            int initialPoint = 0;
            int usePoint = 10000;
            Point point = new Point(1L, initialPoint);
            pointJpaRepository.save(point);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(requestCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            long start = System.currentTimeMillis();

            // act
            for (int i = 0; i < requestCount; i++) {
                executor.submit(() -> {
                    try {
                        pointUseService.useWithLock(1L, usePoint);
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

            long end = System.currentTimeMillis();
            System.out.println("[소요시간]: " + (end - start) + "ms");

            // assert
            System.out.println("[SUCCESS COUNT]: "+successCount.get());
            System.out.println("[FAIL COUNT]: "+failCount.get());

            Point result = pointJpaRepository.findById(point.getId()).orElseThrow();
            System.out.println("[남은 포인트]: " + result.getPoint());
            assertThat(result.getPoint()).isEqualTo(initialPoint);

            List<PointHistory> histories = pointHistoryJpaRepository.findByUserId(1L);
            System.out.println("[포인트 사용 이력 크기]: " + histories.size());
            assertThat(histories).hasSize(0);
        }
    }
}
