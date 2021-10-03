package com.education.common.cache;


import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * cache 接口
 * @author zengjintao
 * @version 1.0
 * @create_at 2019/12/23 21:40
 */
public interface CacheBean {

    <T> T get(String cacheName, Object key);

    <T> T get(Object key);

    void put(String cacheName, Object key, Object value);

    void put(Object key, Object value);

    void put(Object key, Object value, int liveSeconds);

    void put(String cacheName, Object key, Object value, int liveSeconds);

    void put(String cacheName, Object key, Object value, int liveSeconds, TimeUnit timeUnit);

    void put(Object key, Object value, int liveSeconds, TimeUnit timeUnit);

    Collection getKeys(String cacheName);

    Collection getKeys();

    void remove(Object key);

    void remove();

    void remove(String cacheName, Object key);

    void removeAll(String cacheName);
}
