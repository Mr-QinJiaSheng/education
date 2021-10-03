package com.education.common.config;

import com.education.common.cache.CacheBean;
import com.education.common.cache.RedisCacheBean;
import com.education.common.model.JwtToken;
import com.education.common.model.OnlineUserManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/30 21:13
 */
@Configuration
public class BeanConfig {

    private static final String ADMIN_JWT_SECURITY = "education-admin";

    @Bean
    public JwtToken adminJwtToken() {
        return new JwtToken(ADMIN_JWT_SECURITY);
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //配置redisTemplate
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置序列化
        RedisSerializer redisSerializer = new FstRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);//key序列化
        redisTemplate.setValueSerializer(redisSerializer);//value序列化
        redisTemplate.setHashKeySerializer(redisSerializer);//Hash key序列化
        redisTemplate.setHashValueSerializer(redisSerializer);//Hash value序列化
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public OnlineUserManager onlineUserManager(CacheBean redisCacheBean) {
        return new OnlineUserManager(redisCacheBean);
    }

    @Bean
    public CacheBean redisCacheBean(RedisTemplate redisTemplate) {
        return new RedisCacheBean(redisTemplate);
    }
}
