package com.example.config;/**
 * Created by hanqf on 2020/3/23 15:38.
 */


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hanqf
 * @date 2020/3/23 15:38
 */
@Configuration
//<!-- 启用缓存注解 --> <cache:annotation-driven cache-manager="cacheManager" />
@EnableCaching
//注入redis分组配置属性：ttlmap
@ConfigurationProperties(prefix = "caching")
public class RedisConfig extends CachingConfigurerSupport {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分组配置项
    */
    private Map<String, Long> ttlmap;

    public Map<String, Long> getTtlmap() {
        return ttlmap;
    }

    public void setTtlmap(Map<String, Long> ttlmap) {
        this.ttlmap = ttlmap;
    }

    //如果注解式缓存要使用redis，则开启这个bean即可
    @Override
    @Bean
    public CacheManager cacheManager() {
        logger.info("RedisCacheManager");
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory()))
                //缺省配置
                .cacheDefaults(redisCacheConfiguration(3600L))
                //分组配置，不需要分组配置可以去掉，不同的组配置不同的缓存过期时间，可以防止"缓存雪崩"
                .withInitialCacheConfigurations(initialRedisCacheConfiguration())
                .build();
    }

    private RedisCacheConfiguration redisCacheConfiguration(Long ttl) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttl)) //设置过期，单位秒
                //.disableCachingNullValues() //不允许存储null值，默认可以存储null，缓存null可以防止"缓存穿透"
                //.disableKeyPrefix()  //设置key前面不带前缀，最好不要去掉前缀，否则执行删除缓存时会清空全部缓存
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getStringSerializer()))//key字符串
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));//value字符串
    }

    /**
     * <h2针对不同的缓存组配置不同的设置</h2>
     * Created by hanqf on 2020/10/19 15:12. <br>
     *
     * @return java.util.Map&lt;java.lang.String,org.springframework.data.redis.cache.RedisCacheConfiguration&gt;
     * @author hanqf
     */
    private Map<String, RedisCacheConfiguration> initialRedisCacheConfiguration() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        for (Map.Entry<String, Long> entry : ttlmap.entrySet()) {
            redisCacheConfigurationMap.put(entry.getKey(), redisCacheConfiguration(entry.getValue()));
        }
        return redisCacheConfigurationMap;
    }

    //当redis集群中多个服务挂掉后，此时就会抛出异常，导致这个服务都不能使用，所以这里通过继承CachingConfigurerSupport，实现其errorHandler的方法，将异常进行捕获并进行打印，
    // 但是此时系统运行会很缓慢，因为还是要不停的连接挂掉的服务
    @Override
    public CacheErrorHandler errorHandler() {
        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache,
                                            Object key, Object value) {
                RedisErrorException(exception, key);
            }

            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache,
                                            Object key) {
                RedisErrorException(exception, key);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache,
                                              Object key) {
                RedisErrorException(exception, key);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                RedisErrorException(exception, null);
            }
        };
        return cacheErrorHandler;
    }

    protected void RedisErrorException(Exception exception, Object key) {
        logger.error("redis异常：key=[{}]", key, exception);
    }
}
