package com.loopers.utils;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 상품/브랜드/재고/유저/좋아요 더미데이터 생성 유틸
 * <p>
 * Author: ChatGPT (OpenAI)
 * Purpose:
 *   - Brand → Product (1:N), Product → Stock (1:1), User → Like (N:M) 구조에 대한 대량 더미 생성
 * <p>
 * Features:
 *   - 가격/좋아요/재고/타임스탬프 랜덤 분포(현실성 있는 편중 포함)
 *   - Multi-thread + Batch Insert로 대량 삽입 최적화
 *   - 좋아요는 상품별 0~MAX_LIKES_PER_PRODUCT(기본 1000) 개 랜덤 생성, 중복 사용자 방지
 * <p>
 * Usage:
 *   - Standalone: main() 실행 (DB URL/계정정보 조정)
 *   - 테스트: seedAll(connectionSupplier) 호출
 */
public class DummyDataGenerator {

    // ---- TARGET COUNTS ----
    public static final int BRAND_COUNT = 1_000;
    public static final int PRODUCT_COUNT = 1_000_000;
    public static final int STOCK_COUNT = PRODUCT_COUNT;
    public static final int USER_COUNT = 100_000;

    // ---- LIKE GENERATION ----
    public static final int MAX_LIKES_PER_PRODUCT = 1000;
    public static final double LIKES_ZERO_PROB = 0.60;
    public static final int    LIKES_MEAN = 30;

    // ---- TUNING ----
    public static final int PRODUCT_THREADS = 8;
    public static final int STOCK_THREADS   = 8;
    public static final int LIKE_THREADS    = 8;
    public static final int USER_THREADS    = 4;
    public static final int BATCH_SIZE      = 5_000;

    public static void seedAll(Supplier<Connection> connectionSupplier) {
        long t0 = System.currentTimeMillis();
        seedBrands(connectionSupplier, BRAND_COUNT);
        seedUsers(connectionSupplier, USER_COUNT, USER_THREADS);
        seedProducts(connectionSupplier, PRODUCT_COUNT, BRAND_COUNT, PRODUCT_THREADS);
        seedStocks(connectionSupplier, STOCK_COUNT, STOCK_THREADS);
        seedLikesAndSyncProductLikeCount(connectionSupplier, PRODUCT_COUNT, USER_COUNT, LIKE_THREADS);
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

    // ---------- USER (parallel) ----------
    public static void seedUsers(Supplier<Connection> cs, int count, int threads) {
        final List<int[]> ranges = splitRange(count, threads);
        ExecutorService es = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (int[] r : ranges) {
            futures.add(es.submit(() -> insertUsersRange(cs, r[0], r[1])));
        }
        waitAll(es, futures);
    }

    private static void insertUsersRange(Supplier<Connection> cs, int start, int end) {
        final String sql = "INSERT INTO users (id, login_id, email, gender, birth, created_at, updated_at) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = cs.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            ThreadLocalRandom r = ThreadLocalRandom.current();

            for (int i = start; i <= end; i++) {
                String loginId = "user" + i;
                String email   = "user" + i + "@example.com";
                String gender  = r.nextDouble() < 0.5 ? "MALE" : "FEMALE";
                String birthStr = randomBirthDate(1970, 2007).format(DateTimeFormatter.ISO_LOCAL_DATE); // yyyy-MM-dd
                LocalDateTime[] ts = randomRangePast(365 * 10);

                ps.setLong(1, i);
                ps.setString(2, loginId);
                ps.setString(3, email);
                ps.setString(4, gender);
                ps.setString(5, birthStr);
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
            throw new RuntimeException("insertUsersRange failed [" + start + "~" + end + "]", e);
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
                ps.setInt(5, 0);

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

    // ---------- LIKE + product.like_count sync (parallel) ----------
    public static void seedLikesAndSyncProductLikeCount(Supplier<Connection> cs, int productCount, int userCount, int threads) {
        // product를 구간으로 쪼개 병렬 처리
        final List<int[]> ranges = splitRange(productCount, threads);
        ExecutorService es = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (int[] r : ranges) {
            futures.add(es.submit(() -> insertLikesAndUpdateCountsRange(cs, r[0], r[1], userCount)));
        }
        waitAll(es, futures);
    }

    private static void insertLikesAndUpdateCountsRange(Supplier<Connection> cs, int startProductId, int endProductId, int userCount) {
        final String insertLikeSql = "INSERT INTO likes (user_id, product_id, created_at, updated_at) VALUES (?,?,?,?)";
        final String updateProdSql = "UPDATE product SET like_count = ? WHERE id = ?";

        try (Connection conn = cs.get();
             PreparedStatement likePs = conn.prepareStatement(insertLikeSql);
             PreparedStatement prodPs = conn.prepareStatement(updateProdSql)) {

            conn.setAutoCommit(false);
            ThreadLocalRandom r = ThreadLocalRandom.current();

            for (int pid = startProductId; pid <= endProductId; pid++) {
                // 상품별 좋아요 수 샘플링 (0~MAX)
                int likeCount = sampleLikesPerProduct(r);
                if (likeCount > MAX_LIKES_PER_PRODUCT) likeCount = MAX_LIKES_PER_PRODUCT;

                // 유니크 사용자 선정
                if (likeCount > 0) {
                    // 유저 수가 적으면 충돌날 수 있으므로 min 보정
                    int effectiveUserCount = Math.max(userCount, likeCount);
                    // 중복 방지
                    HashSet<Integer> pickedUsers = new HashSet<>(likeCount * 2);
                    while (pickedUsers.size() < likeCount) {
                        int uid = 1 + r.nextInt(effectiveUserCount);
                        pickedUsers.add(uid);
                    }

                    // likes insert
                    for (int uid : pickedUsers) {
                        LocalDateTime[] ts = recentSkewed(365, 2.5);
                        likePs.setInt(1, uid);
                        likePs.setInt(2, pid);
                        likePs.setTimestamp(3, Timestamp.valueOf(ts[0]));
                        likePs.setTimestamp(4, Timestamp.valueOf(ts[1]));
                        likePs.addBatch();
                    }
                }

                // product.like_count 업데이트 (likes 삽입분과 일치)
                prodPs.setInt(1, likeCount);
                prodPs.setInt(2, pid);
                prodPs.addBatch();

                // 배치 플러시
                if (pid % BATCH_SIZE == 0) {
                    likePs.executeBatch();
                    prodPs.executeBatch();
                    conn.commit();
                    likePs.clearBatch();
                    prodPs.clearBatch();
                }
            }

            likePs.executeBatch();
            prodPs.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            throw new RuntimeException("insertLikesAndUpdateCountsRange failed [" + startProductId + "~" + endProductId + "]", e);
        }
    }

    private static int sampleLikesPerProduct(ThreadLocalRandom r) {
        // 60% = 0, 나머지는 지수/포아송 근사로 적당히 분포 (상한 1000)
        if (r.nextDouble() < LIKES_ZERO_PROB) return 0;
        // 포아송 근사: 감마/지수 혼합 대신 간단히 지수 변환
        double lambda = 1.0 / Math.max(1, LIKES_MEAN);
        double u = Math.max(1e-12, r.nextDouble());
        int x = (int) Math.floor(-Math.log(u) / lambda);
        if (x < 1) x = 1;
        return Math.min(x, MAX_LIKES_PER_PRODUCT);
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

    // 재고: 10% 품절, 그 외 Normal 근사 1~200
    private static int sampleQuantity() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        if (r.nextDouble() < 0.10) return 0;
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

    private static LocalDate randomBirthDate(int startYear, int endYearInclusive) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int year = r.nextInt(startYear, endYearInclusive + 1);
        Month month = Month.of(r.nextInt(1, 13));
        int day = Math.min(month.length(false), r.nextInt(1, 29)); // 1~28 안전
        return LocalDate.of(year, month, day);
    }

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/loopers?rewriteBatchedStatements=true";
        String user = "application";
        String pass = "application";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("✅ Connected to DB");
        }

        DummyDataGenerator.seedAll(() -> {
            try {
                return DriverManager.getConnection(url, user, pass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
