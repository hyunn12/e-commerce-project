package com.loopers.application.brand;

import com.loopers.domain.brand.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.loopers.redis.config.CacheConstants.BRANDS_CACHE_KEY;
import static com.loopers.redis.config.CacheConstants.BRANDS_CACHE_TTL;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandCacheService {

    private final BrandService brandService;
    private final RedisTemplate<String, Object> redisTemplate;

    // 단건 조회
    public BrandInfo getCachedBrand(Long brandId) {
        BrandInfo cachedInfo = (BrandInfo) redisTemplate.opsForHash().get(BRANDS_CACHE_KEY, brandId.toString());
        if (cachedInfo != null) return cachedInfo;

        refreshBrandsCache();

        return (BrandInfo) redisTemplate.opsForHash().get(BRANDS_CACHE_KEY, brandId.toString());
    }

    // 브랜드 목록 HSET 저장
    public void refreshBrandsCache() {
        List<BrandInfo> topBrands = brandService.getTopList().stream()
                .map(BrandInfo::from)
                .toList();

        Map<String, BrandInfo> brandInfoMap = topBrands.stream()
                .collect(Collectors.toMap(brandInfo -> brandInfo.getId().toString(), Function.identity()));

        redisTemplate.opsForHash().putAll(BRANDS_CACHE_KEY, brandInfoMap);
        redisTemplate.expire(BRANDS_CACHE_KEY, BRANDS_CACHE_TTL);
    }

    // 단건 갱신
    public void upsertBrand(BrandInfo info) {
        redisTemplate.opsForHash().put(BRANDS_CACHE_KEY, info.getId().toString(), info);
        redisTemplate.expire(BRANDS_CACHE_KEY, BRANDS_CACHE_TTL);
    }
}
