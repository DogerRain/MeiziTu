package com.yudianxx.springBootDemo.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author huangyongwen
 * @date 2020/3/25
 * @Description
 */
@Configuration
public class RedisConfig {
    /**
     *
     * @param factory
     * @return
     * @Desc 解决系列问题
     */
    @Bean("redisStringTemplate")
    public RedisTemplate<Object, Object> redisStringTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
