package com.example.config;/**
 * Created by hanqf on 2020/3/23 15:38.
 */


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    /** 默认日期时间格式 */
    private static final String    DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 默认日期格式 */
    private static final String    DEFAULT_DATE_FORMAT      = "yyyy-MM-dd";
    /** 默认时间格式 */
    private static final String    DEFAULT_TIME_FORMAT      = "HH:mm:ss";


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


    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 参考：https://blog.csdn.net/m0_37589586/article/details/87782001
    */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

        //LocalDateTime系列序列化和反序列化模块，继承自jsr310，我们在这里修改了日期格式
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //序列化
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        //反序列化
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
                DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        //注册模块
        objectMapper.registerModule(javaTimeModule);
        serializer.setObjectMapper(objectMapper);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }


    //如果注解式缓存要使用redis，则开启这个bean即可
    @Bean
    public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
        logger.info("RedisCacheManager");
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory()))
                //缺省配置
                .cacheDefaults(redisCacheConfiguration(3600L,redisTemplate))
                //分组配置，不需要分组配置可以去掉，不同的组配置不同的缓存过期时间，可以防止"缓存雪崩"
                .withInitialCacheConfigurations(initialRedisCacheConfiguration(redisTemplate))
                .build();
    }

    private RedisCacheConfiguration redisCacheConfiguration(Long ttl,RedisTemplate<String, Object> redisTemplate) {

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttl)) //设置过期，单位秒
                //.disableCachingNullValues() //不允许存储null值，默认可以存储null，缓存null可以防止"缓存穿透"
                //.disableKeyPrefix()  //设置key前面不带前缀，最好不要去掉前缀，否则执行删除缓存时会清空全部缓存
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getStringSerializer()))//key字符串
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));//value字符串
    }

    /**
     * <h2针对不同的缓存组配置不同的设置</h2>
     * Created by hanqf on 2020/10/19 15:12. <br>
     *
     * @return java.util.Map&lt;java.lang.String,org.springframework.data.redis.cache.RedisCacheConfiguration&gt;
     * @author hanqf
     */
    private Map<String, RedisCacheConfiguration> initialRedisCacheConfiguration(RedisTemplate<String, Object> redisTemplate) {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        for (Map.Entry<String, Long> entry : ttlmap.entrySet()) {
            redisCacheConfigurationMap.put(entry.getKey(), redisCacheConfiguration(entry.getValue(),redisTemplate));
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
