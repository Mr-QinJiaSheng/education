package com.education.admin.api.shiro;

import com.education.common.cache.CacheBean;
import com.education.common.cache.RedisCacheBean;
import com.education.common.utils.ObjectUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/1 20:53
 */
public class RedisCacheManager implements CacheManager {

    private CacheBean redisCacheBean;

    private final Map<String, Cache> caches = new ConcurrentHashMap();

    public RedisCacheManager(CacheBean redisCacheBean) {
        this.redisCacheBean = redisCacheBean;
    }

    /**
     * 获取缓存对象cache
     * @param s
     * @param <K>
     * @param <V>
     * @return
     * @throws CacheException
     */
    @Override
    public <K, V> Cache<K, V> getCache(String key) throws CacheException {
        return getCacheFormMap(key);
    }

    /**
     * 获取单例Cache对象
     * @param key
     * @param <K>
     * @param <V>
     * @return
     */
    private <K, V> Cache<K, V> getCacheFormMap(String key) {
        Cache cache = caches.get(key);
        if (ObjectUtils.isNotEmpty(cache)) {
            synchronized (this) {
                cache = caches.get(key);
                if (ObjectUtils.isEmpty(cache)) {
                    RedisCache redisCache = new RedisCache(redisCacheBean);
                    caches.put(key, redisCache);
                }
            }
        }
        return cache;
    }
}
