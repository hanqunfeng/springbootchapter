package com.example.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hanqf
 */
@Configuration
@AutoConfigureAfter(value = RedisConfig.class)
//注入redis分组配置属性：ttlmap
@ConfigurationProperties(prefix = "caching")
public class RedisCachingConfig {

    /**
     * 分组配置项
     */
    @Getter
    @Setter
    private Map<String, Long> ttlmap;

    @Bean
    public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory()))
                //缺省配置
                .cacheDefaults(redisCacheConfiguration(redisTemplate, 3600L))
                //分组配置，不需要分组配置可以去掉，不同的组配置不同的缓存过期时间，可以防止"缓存雪崩"
                .withInitialCacheConfigurations(initialRedisCacheConfiguration(redisTemplate))
                .build();
    }

    /**
     * 缺省缓存配置
     */
    private RedisCacheConfiguration redisCacheConfiguration(RedisTemplate<String, Object> redisTemplate, Long ttl) {

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttl)) //设置过期，单位秒
                //.disableCachingNullValues() //不允许存储null值，默认可以存储null，缓存null可以防止"缓存穿透"
                //.disableKeyPrefix()  //设置key前面不带前缀，最好不要去掉前缀，否则执行删除缓存时会清空全部缓存
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getStringSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));
    }

    /**
     * 针对不同的缓存组配置不同的设置
     */
    private Map<String, RedisCacheConfiguration> initialRedisCacheConfiguration(RedisTemplate<String, Object> redisTemplate) {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        for (Map.Entry<String, Long> entry : ttlmap.entrySet()) {
            redisCacheConfigurationMap.put(entry.getKey(), redisCacheConfiguration(redisTemplate, entry.getValue()));
        }
        return redisCacheConfigurationMap;
    }

}
