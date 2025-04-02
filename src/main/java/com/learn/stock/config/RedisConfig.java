package com.learn.stock.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // TTLs by
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                "obsoleteProducts", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)),
                "abcClassification", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1))
        );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
