package com.loopers.utils;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * ProductMetrics 더미데이터 생성 유틸
 * <p>
 * Author: ChatGPT (OpenAI)
 * Purpose:
 *   - 올해 product_metrics 집계 데이터를 대량 생성
 * <p>
 * Features:
 *   - 하루당 1만~2만 raw row 생성 후 productId 기준 집계
 *   - 메트릭 랜덤 분포 범위:
 *       • like_count: 10 ~ 1000
 *       • view_count: 100 ~ 10000
 *       • sales_count: 1 ~ 100
 *   - Multi-thread + Batch Insert로 대량 삽입 최적화
 * <p>
 * Usage:
 *   - Standalone: main() 실행 (DB URL/계정정보 조정)
 *   - 테스트: generateProductMetrics(connectionSupplier) 호출
 */
public class ProductMetricsDummyDataGenerator {

    // ---- 설정 ----
    public static final int MIN_PRODUCT_ID = 1;
    public static final int MAX_PRODUCT_ID = 1000;
    public static final int MIN_ROWS_PER_DAY = 10_000;
    public static final int MAX_ROWS_PER_DAY = 20_000;

    // 메트릭 범위
    public static final int MIN_LIKE_COUNT = 10;
    public static final int MAX_LIKE_COUNT = 1000;
    public static final int MIN_VIEW_COUNT = 100;
    public static final int MAX_VIEW_COUNT = 10000;
    public static final int MIN_SALES_COUNT = 1;
    public static final int MAX_SALES_COUNT = 100;

    // 성능 튜닝
    public static final int THREADS = 8;
    public static final int BATCH_SIZE = 5_000;

    public static void generateProductMetrics(Supplier<Connection> connectionSupplier) {
        long startTime = System.currentTimeMillis();
        System.out.println("[ProductMetricsDummyDataGenerator] Starting...");

        LocalDate startDate = LocalDate.now().withDayOfYear(1); // 올해 1월 1일
        LocalDate endDate = LocalDate.now(); // 오늘까지

        System.out.println("Target period: " + startDate + " ~ " + endDate);

        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dates.add(current);
            current = current.plusDays(1);
        }

        System.out.println("Total days: " + dates.size());

        // 날짜별로 병렬 처리
        List<List<LocalDate>> dateChunks = splitDates(dates, THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        List<Future<?>> futures = new ArrayList<>();

        for (List<LocalDate> chunk : dateChunks) {
            futures.add(executor.submit(() -> processDateChunk(connectionSupplier, chunk)));
        }

        waitAll(executor, futures);

        long endTime = System.currentTimeMillis();
        System.out.println("[ProductMetricsDummyDataGenerator] Completed in " + (endTime - startTime) + " ms");
    }

    private static void processDateChunk(Supplier<Connection> connectionSupplier, List<LocalDate> dates) {
        String sql = "INSERT INTO product_metrics (product_id, metric_date, sales_count, like_count, view_count, updated_at) VALUES (?,?,?,?,?,?)";

        try (Connection conn = connectionSupplier.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int totalInserted = 0;

            for (LocalDate date : dates) {
                int rowsForDay = random.nextInt(MIN_ROWS_PER_DAY, MAX_ROWS_PER_DAY + 1);
                System.out.printf("[Thread %s] Processing %s - generating %d raw rows%n",
                        Thread.currentThread().getName(), date, rowsForDay);

                // 하루 데이터 집계용 Map<productId, Metrics>
                Map<Long, Metrics> dailyMetrics = new HashMap<>();

                for (int i = 0; i < rowsForDay; i++) {
                    long productId = random.nextLong(MIN_PRODUCT_ID, MAX_PRODUCT_ID + 1);

                    long salesCount = random.nextLong(MIN_SALES_COUNT, MAX_SALES_COUNT + 1);
                    long likeCount = random.nextLong(MIN_LIKE_COUNT, MAX_LIKE_COUNT + 1);
                    long viewCount = random.nextLong(MIN_VIEW_COUNT, MAX_VIEW_COUNT + 1);

                    dailyMetrics.merge(productId,
                            new Metrics(salesCount, likeCount, viewCount),
                            Metrics::add);
                }

                // insert
                for (Map.Entry<Long, Metrics> entry : dailyMetrics.entrySet()) {
                    long productId = entry.getKey();
                    Metrics m = entry.getValue();

                    ZonedDateTime updatedAt = date.atStartOfDay()
                            .atZone(java.time.ZoneId.systemDefault())
                            .plusHours(random.nextInt(24))
                            .plusMinutes(random.nextInt(60))
                            .plusSeconds(random.nextInt(60));

                    ps.setLong(1, productId);
                    ps.setDate(2, Date.valueOf(date));
                    ps.setLong(3, m.sales);
                    ps.setLong(4, m.likes);
                    ps.setLong(5, m.views);
                    ps.setTimestamp(6, Timestamp.from(updatedAt.toInstant()));

                    ps.addBatch();
                    totalInserted++;

                    if (totalInserted % BATCH_SIZE == 0) {
                        ps.executeBatch();
                        conn.commit();
                        ps.clearBatch();
                    }
                }
            }

            ps.executeBatch();
            conn.commit();

            System.out.printf("[Thread %s] Completed - inserted %d total rows%n",
                    Thread.currentThread().getName(), totalInserted);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert product metrics data", e);
        }
    }

    private static List<List<LocalDate>> splitDates(List<LocalDate> dates, int chunks) {
        List<List<LocalDate>> result = new ArrayList<>();
        int chunkSize = Math.max(1, dates.size() / chunks);

        for (int i = 0; i < dates.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, dates.size());
            result.add(dates.subList(i, end));
        }

        return result;
    }

    private static void waitAll(ExecutorService executor, List<Future<?>> futures) {
        executor.shutdown();
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Execution failed", e.getCause());
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/loopers?rewriteBatchedStatements=true";
        String user = "application";
        String pass = "application";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("✅ Connected to DB");
        }

        ProductMetricsDummyDataGenerator.generateProductMetrics(() -> {
            try {
                return DriverManager.getConnection(url, user, pass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("✅ ProductMetrics dummy data generation completed!");
    }

    // 내부 클래스: 메트릭 값 누적용
    private static class Metrics {
        long sales;
        long likes;
        long views;

        Metrics(long sales, long likes, long views) {
            this.sales = sales;
            this.likes = likes;
            this.views = views;
        }

        static Metrics add(Metrics a, Metrics b) {
            return new Metrics(a.sales + b.sales, a.likes + b.likes, a.views + b.views);
        }
    }
}
