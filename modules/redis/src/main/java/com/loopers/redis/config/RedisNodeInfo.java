package com.loopers.redis.config;

/**
 * Redis 노드 정보
 */
public record RedisNodeInfo(
        String host,
        int port
) { }
