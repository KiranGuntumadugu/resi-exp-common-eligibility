package com.rogers.api.exception;


import com.rogers.api.logger.CommonLogger;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public class RedisCacheErrorHandler implements CacheErrorHandler {
    private final CommonLogger logger = CommonLogger.getLogger(this.getClass());

    @Override
    public void handleCacheGetError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key) {
        logger.error("cache issue[get], please check redis configuration,key=" + key);
    }

    @Override
    public void handleCachePutError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key, Object value) {
        logger.error("cache issue[put]:, please check redis configuration,key=" + key);
    }

    @Override
    public void handleCacheEvictError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key) {
        logger.error("cache issue[evict], please check redis configuration");
    }

    @Override
    public void handleCacheClearError(@NonNull RuntimeException exception, @NonNull Cache cache) {
        logger.error("cache issue[clear], please check redis configuration");
    }
}
