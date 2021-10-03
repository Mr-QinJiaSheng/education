package com.education.admin.api.shiro;

import com.education.common.cache.CacheBean;
import com.education.common.utils.ObjectUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * redisCache
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/1 20:19
 */
public class RedisCache<K, V> implements Cache<K, V> {

    private CacheBean redisCacheBean;

    public RedisCache(CacheBean redisCacheBean) {
        this.redisCacheBean = redisCacheBean;
    }

    /**
     * 获取缓存key的value
     * @param k
     * @return
     * @throws CacheException
     */
    @Override
    public V get(K k) throws CacheException {
        return redisCacheBean.get(k);
    }

    /**
     * 设置缓存
     * @param key
     * @param value
     * @return
     * @throws CacheException
     */
    @Override
    public V put(K key, V value) throws CacheException {
        redisCacheBean.put(key, value);
        return value;
    }

    /**
     * 删除指定缓存数据
     * @param k
     * @return
     * @throws CacheException
     */
    @Override
    public V remove(K k) throws CacheException {
        V value = redisCacheBean.get(k);
        redisCacheBean.remove(k);
        return value;
    }

    /**
     * 删除redis中的所有缓存
     * @throws CacheException
     */
    @Override
    public void clear() throws CacheException {
        redisCacheBean.remove();
    }

    /**
     * 获取缓存中key大小
     * @return
     */
    @Override
    public int size() {
        return this.keys().size();
    }

    /**
     * 获取缓存中的所有key
     * @return
     */
    @Override
    public Set<K> keys() {
        return (Set<K>) redisCacheBean.getKeys();
    }

    /**
     * 用户获取缓存中的集合对象
     * @return
     */
    @Override
    public Collection<V> values() {
        Collection collection = keys(); // 获取缓存中的所有key
        if (ObjectUtils.isNotEmpty(collection)) {
            Set<V> values = new HashSet<>(); // 用来存储缓存中的所有value集合
            collection.forEach(key -> {
                values.add(this.get((K) key));
            });
            return values;
        }
        return Collections.emptySet();
    }
}
