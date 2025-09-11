package com.loopers.application.ranking;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.infrastructure.product.ProductJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

import static com.loopers.redis.config.CacheConstants.RANKING_PRODUCT_CACHE_KEY_PREFIX;
import static com.loopers.redis.config.CacheConstants.RANKING_PRODUCT_CACHE_MEMBER_KEY;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RankingFacadeIntegrationTest {

    // sut --
    @Autowired
    private RankingFacade rankingFacade;
    @Autowired
    private StringRedisTemplate redisTemplate;

    // orm --
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private RedisCleanUp redisCleanUp;

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
        databaseCleanUp.truncateAllTables();
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 랭킹_목록_조회_시 {

        @DisplayName("정상적으로 랭킹 목록을 조회한다.")
        @Test
        void returnRankingList() {
            // arrange
            String date = "20250911";
            String key = RANKING_PRODUCT_CACHE_KEY_PREFIX + date;

            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            Product product1 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품1").price(10000).build());
            Product product2 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품2").price(20000).build());
            Product product3 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품3").price(30000).build());
            double score1 = 99.9;
            double score2 = 88.8;
            double score3 = 77.7;

            redisTemplate.opsForZSet().add(key, RANKING_PRODUCT_CACHE_MEMBER_KEY + product1.getId(), score1);
            redisTemplate.opsForZSet().add(key, RANKING_PRODUCT_CACHE_MEMBER_KEY + product2.getId(), score2);
            redisTemplate.opsForZSet().add(key, RANKING_PRODUCT_CACHE_MEMBER_KEY + product3.getId(), score3);

            RankingCommand.Summary command = RankingCommand.Summary.builder().date(date).page(0).size(5).build();

            // act
            RankingInfo.Summary result = rankingFacade.getList(command);

            // assert
            assertThat(result.getRankings()).hasSize(3);
            assertThat(result.getTotalCount()).isEqualTo(3);

            List<RankingInfo.Item> rankings = result.getRankings();
            assertThat(rankings.get(0).getScore()).isEqualTo(score1);
            assertThat(rankings.get(0).getRank()).isEqualTo(1);
            assertThat(rankings.get(0).getProductName()).isEqualTo(product1.getName());
            assertThat(rankings.get(0).getBrandName()).isEqualTo(product1.getBrand().getName());
        }

        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        @Nested
        class 랭킹_키가_존재하지_않을_경우 {

            @DisplayName("전일자 데이터가 존재한다면 해당 랭킹 목록을 반환한다.")
            @Test
            void returnYesterdayRankingList() {
                // arrange
                String date = "20250911";
                String key = RANKING_PRODUCT_CACHE_KEY_PREFIX + date;

                Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
                Product product1 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품1").price(10000).build());
                Product product2 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품2").price(20000).build());
                Product product3 = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품3").price(30000).build());
                double score1 = 99.9;
                double score2 = 88.8;
                double score3 = 77.7;

                redisTemplate.opsForZSet().add(key, RANKING_PRODUCT_CACHE_MEMBER_KEY + product1.getId(), score1);
                redisTemplate.opsForZSet().add(key, RANKING_PRODUCT_CACHE_MEMBER_KEY + product2.getId(), score2);
                redisTemplate.opsForZSet().add(key, RANKING_PRODUCT_CACHE_MEMBER_KEY + product3.getId(), score3);

                RankingCommand.Summary command = RankingCommand.Summary.builder().date("20250912").page(0).size(5).build();

                // act
                RankingInfo.Summary result = rankingFacade.getList(command);

                // assert
                assertThat(result.getRankings()).hasSize(3);
                assertThat(result.getTotalCount()).isEqualTo(3);

                List<RankingInfo.Item> rankings = result.getRankings();
                assertThat(rankings.get(0).getScore()).isEqualTo(score1);
                assertThat(rankings.get(0).getRank()).isEqualTo(1);
                assertThat(rankings.get(0).getProductName()).isEqualTo(product1.getName());
                assertThat(rankings.get(0).getBrandName()).isEqualTo(product1.getBrand().getName());
            }

            @DisplayName("빈 결과를 반환한다.")
            @Test
            void returnEmpty() {
                // arrange
                RankingCommand.Summary command = RankingCommand.Summary.builder().date("20250910").page(0).size(5).build();

                // act
                RankingInfo.Summary result = rankingFacade.getList(command);

                // assert
                assertThat(result.getRankings()).isEmpty();
                assertThat(result.getTotalCount()).isEqualTo(0);
            }
        }
    }

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    @Nested
    class 페이징_처리_시 {

        @DisplayName("페이징이 정상적으로 작동한다.")
        @Test
        void handlePaging() {
            // arrange
            String date = "20250912";
            String key = RANKING_PRODUCT_CACHE_KEY_PREFIX + date;

            int page = 1; // 2페이지
            int size = 3;
            int totalCount = 10;

            int rank1 = 4;
            int rank2 = 5;
            int rank3 = 6;
            double score1 = 0;
            double score2 = 0;
            double score3 = 0;

            Brand brand = brandJpaRepository.save(Brand.builder().name("브랜드").description("설명").build());
            for (int i = 1; i <= totalCount; i++) {
                Product product = productJpaRepository.save(Product.createBuilder().brand(brand).name("상품" + i).price(i*10000).build());
                double score = 100.0 - i;
                redisTemplate.opsForZSet().add(key, RANKING_PRODUCT_CACHE_MEMBER_KEY + product.getId(), score);
                if (i == rank1) score1 = score;
                if (i == rank2) score2 = score;
                if (i == rank3) score3 = score;
            }

            RankingCommand.Summary command = RankingCommand.Summary.builder().date(date).page(page).size(size).build();

            // act
            RankingInfo.Summary result = rankingFacade.getList(command);

            // assert
            assertThat(result.getRankings()).hasSize(size);
            assertThat(result.getTotalCount()).isEqualTo(totalCount);

            List<RankingInfo.Item> rankings = result.getRankings();

            assertThat(rankings.get(0).getRank()).isEqualTo(rank1);
            assertThat(rankings.get(1).getRank()).isEqualTo(rank2);
            assertThat(rankings.get(2).getRank()).isEqualTo(rank3);

            assertThat(rankings.get(0).getScore()).isEqualTo(score1);
            assertThat(rankings.get(1).getScore()).isEqualTo(score2);
            assertThat(rankings.get(2).getScore()).isEqualTo(score3);
        }
    }
}
