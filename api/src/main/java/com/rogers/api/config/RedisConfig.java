package com.rogers.api.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rogers.api.exception.RedisCacheErrorHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis cache configuration.
 * <p>
 * Registers {@link RedisCacheManager} with per-cache TTL support driven by
 * {@code spring.data.redis.caches-ttl} entries in application.yml, with a
 * global default of {@code spring.data.redis.default-ttl} seconds.
 * <p>
 * Wires {@link RedisCacheErrorHandler} so Redis failures are logged rather than
 * propagated to callers, keeping the service resilient when Redis is degraded.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisConfig implements CachingConfigurer {

    private long defaultTtl = 3600;
    private Map<String, Long> cachesTtl = new HashMap<>();

    // ── ConfigurationProperties setters ───────────────────────────────────────

    public void setDefaultTtl(long defaultTtl) {
        this.defaultTtl = defaultTtl;
    }

    public void setCachesTtl(Map<String, Long> cachesTtl) {
        this.cachesTtl = cachesTtl != null ? cachesTtl : Collections.emptyMap();
    }

    // ── Beans ─────────────────────────────────────────────────────────────────

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = buildCacheConfig(defaultTtl);

        Map<String, RedisCacheConfiguration> perCacheConfigs = new HashMap<>();
        cachesTtl.forEach((cacheName, ttl) ->
                perCacheConfigs.put(cacheName, buildCacheConfig(ttl)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(perCacheConfigs)
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private RedisCacheConfiguration buildCacheConfig(long ttlSeconds) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttlSeconds))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(redisObjectMapper())));
    }

    private ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        return mapper;
    }
}
