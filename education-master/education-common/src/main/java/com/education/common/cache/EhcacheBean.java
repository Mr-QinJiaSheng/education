package com.education.common.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 基于Ehcache 缓存
 * @author zengjintao
 * @version 1.0
 * @create_at 2019/12/23 21:11
 */
public class EhcacheBean implements CacheBean {

    private static final String DEFAULT_CACHE = "default_cache";
    private final CacheManager cacheManager;
    private static final Logger logger = LoggerFactory.getLogger(EhcacheBean.class);
    private final Object rock = new Object();

    public EhcacheBean() {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("ehcache.xml");
        this.cacheManager = CacheManager.create(inputStream);
    }

    public EhcacheBean(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    private Cache getOrAddCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            synchronized (rock) {
                cache = cacheManager.getCache(cacheName);
                if (cache == null) {
                    logger.warn("Could not find cache config [" + cacheName + "], using default.");
                    cacheManager.addCacheIfAbsent(cacheName);
                    cache = cacheManager.getCache(cacheName);
                    logger.debug("Cache [" + cacheName + "] started.");
                }
            }
        }
        return cache;
    }

    public void put(String cacheName, Object key, Object value) {
        getOrAddCache(cacheName).put(new Element(key, value));
    }

    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds) {
        if (liveSeconds <= 0) {
            this.put(cacheName, key, value);
        } else {
            Element element = new Element(key, value);
            element.setTimeToLive(liveSeconds);
            this.getOrAddCache(cacheName).put(element);
        }
    }

    @Override
    public void put(String cacheName, Object key, Object value, int liveSeconds, TimeUnit timeUnit) {
        this.put(cacheName, key, value, liveSeconds);
    }

    @Override
    public void put(Object key, Object value, int liveSeconds, TimeUnit timeUnit) {
        this.put(key, value, liveSeconds);
    }

    public void put(Object key, Object value) {
        put(DEFAULT_CACHE, key, value);
    }

    @Override
    public void put(Object key, Object value, int liveSeconds) {
        if (liveSeconds <= 0) {
            this.put(key, value);
        } else {
            Element element = new Element(key, value);
            element.setTimeToLive(liveSeconds);
            this.getOrAddCache(DEFAULT_CACHE).put(element);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String cacheName, Object key) {
        Element element = getOrAddCache(cacheName).get(key);
        return element != null ? (T)element.getObjectValue() : null;
    }

    public <T> T get(Object key) {
        return get(DEFAULT_CACHE, key);
    }

    @SuppressWarnings("rawtypes")
    public Collection getKeys(String cacheName) {
        return getOrAddCache(cacheName).getKeys();
    }

    @Override
    public Collection getKeys() {
        return this.getKeys(DEFAULT_CACHE);
    }

    @Override
    public void remove(Object key) {
        this.remove(DEFAULT_CACHE, key);
    }

    @Override
    public void remove() {
        this.remove(DEFAULT_CACHE);
    }

    public void remove(String cacheName, Object key) {
        getOrAddCache(cacheName).remove(key);
    }

    public void removeAll(String cacheName) {
        getOrAddCache(cacheName).removeAll();
    }
}
