package com.laddco.branchdemo.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Configuration class for setting up caching with Caffeine.
 *
 * Went with caffeine since it has an in-memory implementation of a cache that supports TTL and is easy to set up with Spring Boot.
 * Had this been real production, I would probably use something like Redis for persistence and scaling, but for the sake of this demo, Caffeine is sufficient.
 */
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("gitHubData");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES));
        return cacheManager;
    }
}
