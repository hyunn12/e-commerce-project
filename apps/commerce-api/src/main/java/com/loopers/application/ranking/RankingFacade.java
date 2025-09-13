package com.loopers.application.ranking;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingFacade {

    private final RankingService rankingService;
    private final ProductService productService;
    private final BrandService brandService;

    @Transactional(readOnly = true)
    public RankingInfo.Summary getList(RankingCommand.Summary command) {
        String key = rankingService.buildRankingKey(command.getDate());
        Pageable pageable = command.toPageable();

        if (!rankingService.existsRankingKey(key)) {
            log.warn("Ranking key not found: {}", key);
            return tryFallbackRanking(command, pageable);
        }

        return getFromRedis(key, pageable);
    }

    private RankingInfo.Summary getFromRedis(String key, Pageable pageable) {
        List<RankingRaw> raws = rankingService.getTopRankings(key, pageable);
        long totalCount = rankingService.getTotalRankingCount(key);

        if (raws.isEmpty()) {
            return RankingInfo.Summary.empty(pageable);
        }

        List<Long> productIds = raws.stream().map(RankingRaw::productId).toList();
        List<Product> products = productService.getListByIds(productIds);
        if (products.isEmpty()) {
            return RankingInfo.Summary.empty(pageable);
        }

        List<Long> brandIds = products.stream().map(p -> p.getBrand().getId()).distinct().toList();
        List<Brand> brands = brandService.getListByIds(brandIds);

        return RankingInfo.Summary.from(raws, products, brands, pageable, totalCount);
    }

    private RankingInfo.Summary tryFallbackRanking(RankingCommand.Summary command, Pageable pageable) {
        LocalDate yesterday = rankingService.parseDate(command.getDate()).minusDays(1);
        String key = rankingService.buildRankingKey(yesterday);

        if (rankingService.existsRankingKey(key)) {
            log.info("Ranking Fallback: {}", key);
            return getFromRedis(key, pageable);
        } else {
            return RankingInfo.Summary.empty(pageable);
        }
    }

    public void warmUpTomorrowRanking() {
        // 오늘 날짜 Key
        LocalDate today = LocalDate.now();
        String todayKey = rankingService.buildRankingKey(today);

        // 내일 날짜 Key
        LocalDate tomorrow = today.plusDays(1);
        String tomorrowKey = rankingService.buildRankingKey(tomorrow);

        // 오늘 데이터 Top100 가져오기
        Pageable top100 = PageRequest.of(0, 100);
        List<RankingRaw> raws = rankingService.getTopRankings(todayKey, top100);

        if (raws.isEmpty()) {
            log.warn("오늘 랭킹 데이터가 없습니다: todayKey={}", todayKey);
            return;
        }

        // 이미 데이터가 있는지 확인
        if (rankingService.getTotalRankingCount(tomorrowKey) > 0) {
            log.info("내일 랭킹 데이터가 이미 존재합니다: tomorrowKey= {}", tomorrowKey);
            return;
        }

        // 내일 키에 추가
        rankingService.warmUpTomorrow(tomorrowKey, raws);
    }
}
