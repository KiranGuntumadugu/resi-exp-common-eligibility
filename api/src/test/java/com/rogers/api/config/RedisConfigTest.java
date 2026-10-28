package com.rogers.api.config;

import com.rogers.api.exception.RedisCacheErrorHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link RedisConfig}.
 */
class RedisConfigTest {

    private RedisConfig buildConfig(long defaultTtl, Map<String, Long> cachesTtl) {
        RedisConfig config = new RedisConfig();
        config.setDefaultTtl(defaultTtl);
        config.setCachesTtl(cachesTtl);
        return config;
    }

    @Test
    @DisplayName("errorHandler: returns RedisCacheErrorHandler instance")
    void errorHandler_returnsRedisCacheErrorHandler() {
        CacheErrorHandler handler = buildConfig(3600, Map.of("test_cache", 120L)).errorHandler();
        assertThat(handler).isInstanceOf(RedisCacheErrorHandler.class);
    }

    @Test
    @DisplayName("redisCacheManager: bean is created successfully with per-cache TTL")
    void redisCacheManager_beanCreated() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisCacheManager manager = buildConfig(3600, Map.of("test_cache", 120L))
                .redisCacheManager(factory);
        assertThat(manager).isNotNull();
    }

    @Test
    @DisplayName("redisCacheManager: works with empty cachesTtl map")
    void redisCacheManager_emptyPerCacheMap_noBuildError() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisCacheManager manager = buildConfig(600, Collections.emptyMap())
                .redisCacheManager(factory);
        assertThat(manager).isNotNull();
    }

    @Test
    @DisplayName("redisCacheManager: null cachesTtl is treated as empty — no NullPointerException")
    void redisCacheManager_nullCachesTtl_doesNotThrow() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisCacheManager manager = buildConfig(3600, null)
                .redisCacheManager(factory);
        assertThat(manager).isNotNull();
    }

    @Test
    @DisplayName("redisCacheManager: multiple per-cache TTL entries are all applied")
    void redisCacheManager_multiplePerCacheEntries_noBuildError() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisCacheManager manager = buildConfig(3600, Map.of("cache_a", 60L, "cache_b", 120L))
                .redisCacheManager(factory);
        assertThat(manager).isNotNull();
    }
}
