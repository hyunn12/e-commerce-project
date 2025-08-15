package com.loopers.application.brand;

import com.loopers.domain.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.loopers.config.redis.CacheConstants.BRANDS_CACHE_KEY;
import static com.loopers.config.redis.CacheConstants.BRANDS_CACHE_TTL;

@Service
@RequiredArgsConstructor
public class BrandCacheService {

    private final BrandService brandService;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<BrandInfo> getCachedBrands(int limit) {
        // 캐시 조회
        BrandInfoList cachedBrands = (BrandInfoList) redisTemplate.opsForValue().get(BRANDS_CACHE_KEY);
        if (cachedBrands != null) {
            return cachedBrands.getBrandInfos();
        }

        // DB 조회
        List<BrandInfo> topBrands = brandService.getTopList(limit)
                .stream()
                .map(BrandInfo::from)
                .toList();
        BrandInfoList list = BrandInfoList.of(topBrands);

        // 캐시 저장
        redisTemplate.opsForValue().set(BRANDS_CACHE_KEY, list, BRANDS_CACHE_TTL);

        return topBrands;
    }

}
