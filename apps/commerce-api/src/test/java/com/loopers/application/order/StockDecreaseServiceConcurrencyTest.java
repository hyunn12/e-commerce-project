package com.loopers.application.order;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.Stock;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.infrastructure.product.StockJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("재고 차감에 비관적 락 적용 시")
@SpringBootTest
class StockDecreaseServiceConcurrencyTest {
    // sut --
    @Autowired
    private StockDecreaseService stockDecreaseService;

    // orm --
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private StockJpaRepository stockJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("재고가 충분하다면 모든 주문에 대해 재고가 정확히 차감된다.")
    @Test
    void decreaseStock() throws Exception {
        // arrange
        int threadCount = 50;
        int requestCount = 1000;

        int initialStock = 10000;
        int decreaseStock = 1;

        Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
        Product product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품").price(10000).build());
        Stock stock = stockJpaRepository.save(Stock.builder().product(product).quantity(initialStock).build());

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(requestCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // act
        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                try {
                    stockDecreaseService.decrease(product.getId(), decreaseStock);
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

        Stock result = stockJpaRepository.findById(stock.getId()).orElseThrow();
        System.out.println("[남은 재고]:" + result.getQuantity());
        assertThat(result.getQuantity()).isEqualTo(initialStock-(decreaseStock*requestCount));
    }

    @DisplayName("재고가 부족하다면 유효한 재고에 대해서만 차감된다.")
    @Test
    void decreaseStock_whenStockIsNotEnough() throws Exception {
        // arrange
        int threadCount = 50;
        int requestCount = 1000;

        int initialStock = 500;
        int decreaseStock = 1;

        Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
        Product product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품").price(10000).build());
        Stock stock = stockJpaRepository.save(Stock.builder().product(product).quantity(initialStock).build());

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(requestCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // act
        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                try {
                    stockDecreaseService.decrease(product.getId(), decreaseStock);
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

        Stock result = stockJpaRepository.findById(stock.getId()).orElseThrow();
        System.out.println("[남은 재고]:" + result.getQuantity());
        assertThat(result.getQuantity()).isEqualTo(initialStock-(decreaseStock*successCount.get()));
    }

    @DisplayName("재고가 0이라면 모든 주문에 대해 재고 차감이 실패한다.")
    @Test
    void decreaseStock_whenStockIsZero() throws Exception {
        // arrange
        int threadCount = 50;
        int requestCount = 1000;

        int initialStock = 0;
        int decreaseStock = 1;

        Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
        Product product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품").price(10000).build());
        Stock stock = stockJpaRepository.save(Stock.builder().product(product).quantity(initialStock).build());

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(requestCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // act
        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                try {
                    stockDecreaseService.decrease(product.getId(), decreaseStock);
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

        Stock result = stockJpaRepository.findById(stock.getId()).orElseThrow();
        System.out.println("[남은 재고]:" + result.getQuantity());
        assertThat(result.getQuantity()).isEqualTo(initialStock);
    }

}
