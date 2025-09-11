package com.loopers.redis.config;

import java.time.Duration;

public final class CacheConstants {
    private CacheConstants() {}

    public static final int BRAND_CACHE_LIMIT = 50;
    public static final int PRODUCT_CACHE_LIMIT = 100;

    public static final String RANKING_DATE_PATTERN = "yyyyMMdd";

    public static final Duration BRANDS_CACHE_TTL = Duration.ofDays(1);
    public static final Duration PRODUCTS_CACHE_TTL = Duration.ofHours(1);
    public static final Duration RANKING_CACHE_TTL = Duration.ofDays(2);

    public static final String BRANDS_CACHE_KEY = "top:brand:list";
    public static final String PRODUCTS_CACHE_KEY_PREFIX = "top:brand:";
    public static final String PRODUCTS_CACHE_KEY_SUFFIX = ":products";
    public static final String RANKING_PRODUCT_CACHE_KEY_PREFIX = "ranking:product:all:";
    public static final String RANKING_PRODUCT_CACHE_MEMBER_KEY = "product:";

}
