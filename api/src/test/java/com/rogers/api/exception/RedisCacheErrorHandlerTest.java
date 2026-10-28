package com.rogers.api.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RedisCacheErrorHandler}.
 * Verifies that every cache error callback is handled gracefully (logged, not
 * re-thrown) so a Redis outage does not cascade into the caller.
 */
class RedisCacheErrorHandlerTest {

    private RedisCacheErrorHandler handler;
    private Cache cache;
    private RuntimeException ex;

    @BeforeEach
    void setUp() {
        handler = new RedisCacheErrorHandler();
        cache   = mock(Cache.class);
        when(cache.getName()).thenReturn("test_cache");
        ex = new RuntimeException("Redis connection refused");
    }

    @Test
    @DisplayName("handleCacheGetError: logs and does not throw")
    void handleCacheGetError_doesNotThrow() {
        assertDoesNotThrow(() -> handler.handleCacheGetError(ex, cache, "some-key"));
    }

    @Test
    @DisplayName("handleCachePutError: logs and does not throw")
    void handleCachePutError_doesNotThrow() {
        assertDoesNotThrow(() -> handler.handleCachePutError(ex, cache, "some-key", "some-value"));
    }

    @Test
    @DisplayName("handleCacheEvictError: logs and does not throw")
    void handleCacheEvictError_doesNotThrow() {
        assertDoesNotThrow(() -> handler.handleCacheEvictError(ex, cache, "some-key"));
    }

    @Test
    @DisplayName("handleCacheClearError: logs and does not throw")
    void handleCacheClearError_doesNotThrow() {
        assertDoesNotThrow(() -> handler.handleCacheClearError(ex, cache));
    }
}

