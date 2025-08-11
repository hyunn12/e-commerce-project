package com.loopers.utils;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * ===============================================
 * ProductDummyDataGenerator
 * -----------------------------------------------
 * Author: ChatGPT (OpenAI)
 * Purpose: 브랜드(Brand), 상품(Product), 재고(Stock) 더미 데이터를 대량 생성하여 병렬로 Insert 하는 유틸
 * Features:
 *   - Brand → Product (1:N), Product → Stock (1:1) 관계 반영
 *   - Brand ID는 α=2.2의 Skewed Distribution (편중 분포)로 할당
 *   - 가격은 85%-10%-5% 구간별로 랜덤 생성 (천 단위)
 *   - 좋아요 수는 지수 분포 기반 랜덤 생성
 *   - 재고는 10% 품절, 나머지는 Normal 근사 분포(1~200개)
 *   - Multi-thread + Batch Insert로 대용량 데이터 삽입 속도 최적화
 * Usage:
 *   - Standalone 실행: main() 메서드 실행 후 DB URL/계정정보 수정
 *   - 테스트 코드 내: seedAll() 호출 시 DataSource로부터 Connection 주입
 * ===============================================
 */
public class ProductDummyDataGenerator {

    // ---- TARGET COUNTS ----
    public static final int BRAND_COUNT   = 1_000;
    public static final int PRODUCT_COUNT = 1_000_000;
    public static final int STOCK_COUNT   = 1_000_000;

    // ---- TUNING ----
    public static final int PRODUCT_THREADS = 8;
    public static final int STOCK_THREADS   = 8;
    public static final int BATCH_SIZE      = 5_000;

    public static void seedAll(Supplier<Connection> connectionSupplier) {
        long t0 = System.currentTimeMillis();
        seedBrands(connectionSupplier, BRAND_COUNT);
        seedProducts(connectionSupplier, PRODUCT_COUNT, BRAND_COUNT, PRODUCT_THREADS);
        seedStocks(connectionSupplier, STOCK_COUNT, STOCK_THREADS);
        long t1 = System.currentTimeMillis();
        System.out.println("[ProductDummyDataGenerator] done in " + (t1 - t0) + " ms");
    }

    // ---------- BRAND (single-thread) ----------
    public static void seedBrands(Supplier<Connection> cs, int count) {
        final String sql = "INSERT INTO brand (id, name, description, created_at, updated_at) VALUES (?,?,?,?,?)";
        try (Connection conn = cs.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (int i = 1; i <= count; i++) {
                ps.setLong(1, i);
                ps.setString(2, "브랜드 " + i);
                ps.setString(3, "브랜드 " + i + " 설명");

                LocalDateTime[] ts = randomRangePast(365 * 3);
                ps.setTimestamp(4, Timestamp.valueOf(ts[0]));
                ps.setTimestamp(5, Timestamp.valueOf(ts[1]));

                ps.addBatch();
                if (i % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    conn.commit();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("seedBrands failed", e);
        }
    }

    // ---------- PRODUCT (parallel) ----------
    public static void seedProducts(Supplier<Connection> cs, int count, int brandCount, int threads) {
        final List<int[]> ranges = splitRange(count, threads);
        ExecutorService es = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (int[] r : ranges) {
            futures.add(es.submit(() -> insertProductsRange(cs, r[0], r[1], brandCount)));
        }
        waitAll(es, futures);
    }

    private static void insertProductsRange(Supplier<Connection> cs, int start, int end, int brandCount) {
        final String sql = "INSERT INTO product (id, brand_id, name, price, like_count, created_at, updated_at) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = cs.get();
             PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            conn.setAutoCommit(false);

            for (int i = start; i <= end; i++) {
                int brandId = skewedBrandId(brandCount, 2.2); // 상위 브랜드로 몰림(Zipf-ish)
                ps.setLong(1, i);
                ps.setInt(2, brandId);
                ps.setString(3, "상품 " + i);
                ps.setInt(4, samplePrice());
                ps.setInt(5, sampleLike());

                LocalDateTime[] ts = recentSkewed(365, 2.3);
                ps.setTimestamp(6, Timestamp.valueOf(ts[0]));
                ps.setTimestamp(7, Timestamp.valueOf(ts[1]));

                ps.addBatch();
                if (i % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    conn.commit();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("insertProductsRange failed [" + start + "~" + end + "]", e);
        }
    }

    // ---------- STOCK (parallel, 1:1 with product) ----------
    public static void seedStocks(Supplier<Connection> cs, int count, int threads) {
        final List<int[]> ranges = splitRange(count, threads);
        ExecutorService es = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (int[] r : ranges) {
            futures.add(es.submit(() -> insertStocksRange(cs, r[0], r[1])));
        }
        waitAll(es, futures);
    }

    private static void insertStocksRange(Supplier<Connection> cs, int start, int end) {
        final String sql = "INSERT INTO stock (id, product_id, quantity, created_at, updated_at) VALUES (?,?,?,?,?)";
        try (Connection conn = cs.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (int i = start; i <= end; i++) {
                ps.setLong(1, i);
                ps.setLong(2, i);

                ps.setInt(3, sampleQuantity());

                LocalDateTime[] ts = recentSkewed(90, 1.0);
                ps.setTimestamp(4, Timestamp.valueOf(ts[0]));
                ps.setTimestamp(5, Timestamp.valueOf(ts[1]));

                ps.addBatch();
                if (i % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    conn.commit();
                    ps.clearBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("insertStocksRange failed [" + start + "~" + end + "]", e);
        }
    }

    // ---------- Helpers ----------

    private static List<int[]> splitRange(int total, int threads) {
        List<int[]> ranges = new ArrayList<>(threads);
        int base = total / threads;
        int rem = total % threads;
        int cur = 1;
        for (int t = 0; t < threads; t++) {
            int size = base + (t < rem ? 1 : 0);
            int start = cur;
            int end = cur + size - 1;
            if (size > 0) ranges.add(new int[]{start, end});
            cur = end + 1;
        }
        return ranges;
    }

    private static void waitAll(ExecutorService es, List<Future<?>> futures) {
        es.shutdown();
        for (Future<?> f : futures) {
            try { f.get(); }
            catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            catch (ExecutionException ee) { throw new RuntimeException(ee.getCause()); }
        }
    }

    // 상위로 몰리는 brandId
    private static int skewedBrandId(int brandCount, double alpha) {
        double u = ThreadLocalRandom.current().nextDouble();
        int id = 1 + (int) Math.floor(Math.pow(u, alpha) * brandCount);
        if (id < 1) id = 1;
        if (id > brandCount) id = brandCount;
        return id;
    }

    // 가격: 저가 다수 + 고가 소수 (혼합 분포)
    private static int samplePrice() {
        double rand = Math.random();
        int step = 1000;

        if (rand < 0.85) {
            // 85%: 1,000 ~ 50,000
            int min = 1000;
            int max = 50_000;
            return ((int) (Math.random() * ((double) (max - min) / step + 1)) * step) + min;
        } else if (rand < 0.95) {
            // 10%: 51,000 ~ 300,000
            int min = 51_000;
            int max = 300_000;
            return ((int) (Math.random() * ((double) (max - min) / step + 1)) * step) + min;
        } else {
            // 5%: 301,000 ~ 1,000,000
            int min = 301_000;
            int max = 1_000_000;
            return ((int) (Math.random() * ((double) (max - min) / step + 1)) * step) + min;
        }
    }

    // 좋아요: 지수 분포 근사 (상위 소수만 큼)
    private static int sampleLike() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        double lambda = 1.0 / 600.0; // 평균 ~600
        double u = Math.max(1e-12, r.nextDouble());
        int x = (int) Math.floor(-Math.log(u) / lambda);
        return Math.min(x, 50_000);
    }

    // 재고: 10% 품절, 그 외 Normal 근사 1~200
    private static int sampleQuantity() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        if (r.nextDouble() < 0.10) return 0; // 10% 품절
        double n = normal01();
        int q = (int) Math.round(30 + 25 * n);
        if (q < 1) q = 1;
        if (q > 200) q = 200;
        return q;
    }

    private static double normal01() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        double u1 = Math.max(1e-12, r.nextDouble());
        double u2 = r.nextDouble();
        return Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
    }

    private static LocalDateTime[] recentSkewed(int daysRange, double alpha) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        double u = r.nextDouble();
        int daysBack = (int) Math.floor(Math.pow(u, alpha) * daysRange);
        LocalDateTime created = LocalDateTime.now(ZoneId.systemDefault())
                .minusDays(daysBack)
                .minusHours(r.nextInt(0, 24))
                .minusMinutes(r.nextInt(0, 60));
        LocalDateTime updated = created.plusHours(r.nextInt(0, 240));
        return new LocalDateTime[]{created, updated};
    }

    private static LocalDateTime[] randomRangePast(int daysBackMax) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int d = r.nextInt(0, daysBackMax + 1);
        LocalDateTime c = LocalDateTime.now(ZoneId.systemDefault())
                .minusDays(d)
                .minusHours(r.nextInt(0, 24))
                .minusMinutes(r.nextInt(0, 60));
        LocalDateTime u = c.plusHours(r.nextInt(0, 240));
        return new LocalDateTime[]{c, u};
    }

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/loopers?rewriteBatchedStatements=true";
        String user = "application";
        String pass = "application";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("✅ Connected to DB");
        }

        ProductDummyDataGenerator.seedAll(() -> {
            try {
                return DriverManager.getConnection(url, user, pass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
