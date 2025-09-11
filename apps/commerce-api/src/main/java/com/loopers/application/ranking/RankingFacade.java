package com.loopers.application.ranking;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
