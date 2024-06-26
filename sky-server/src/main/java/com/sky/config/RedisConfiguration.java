/**
 * @Author：乐
 * @Package：com.sky.config
 * @Project：sky-take-out
 * @name：RedisConfiguration
 * @Date：2024/3/4 0004  20:58
 * @Filename：RedisConfiguration
 */
package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis的配置类
 */
@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
//    @ConditionalOnSingleCandidate
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建redis模板对象");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis的连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
