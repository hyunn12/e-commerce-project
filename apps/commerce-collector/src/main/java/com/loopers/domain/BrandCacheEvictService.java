package com.loopers.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.loopers.config.redis.CacheConstants.BRANDS_CACHE_KEY;

@Service
@RequiredArgsConstructor
public class BrandCacheEvictService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void evict(Long brandId) {
        redisTemplate.opsForHash().delete(BRANDS_CACHE_KEY, brandId.toString());
    }
}
