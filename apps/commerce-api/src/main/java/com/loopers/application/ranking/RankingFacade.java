package com.loopers.application.ranking;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        switch (command.getType()) {
            case DAILY -> {
                return getDailyRanking(command.getDate(), command.toPageable());
            }
            default -> throw new IllegalArgumentException("Unsupported ranking type: " + command.getType());
        }
    }

    @Transactional(readOnly = true)
    public RankingInfo.Summary getDailyRanking(String date, Pageable pageable) {
        List<RankingRaw> raws = rankingService.getDailyRankings(date, pageable);
        long totalCount = rankingService.getTotalRankingCount(date);

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

    public void warmUpTomorrowRanking() {
        rankingService.warmUpTomorrow();
    }
}
