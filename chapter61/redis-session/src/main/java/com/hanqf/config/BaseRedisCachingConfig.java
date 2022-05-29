package com.hanqf.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * <h1>基础配置</h1>
 */

public class BaseRedisCachingConfig extends CachingConfigurerSupport {
    private static final Logger logger = LoggerFactory.getLogger(BaseRedisCachingConfig.class);

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 分组配置项
     */
    private Map<String, Long> ttlmap;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;



    public Map<String, Long> getTtlmap() {
        return ttlmap;
    }

    public void setTtlmap(Map<String, Long> ttlmap) {
        this.ttlmap = ttlmap;
    }

    /**
     * 参考：https://blog.csdn.net/m0_37589586/article/details/87782001
     * Json序列化和反序列化转换器，用于转换Post请求体中的json以及将我们的对象序列化为返回响应的json
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        ////解决java.lang.ClassCastException: DTOObject cannot be cast to DTOObject，此时同时支持spring-devtools和redis
        ////参考：https://stackoverflow.com/questions/37977166/java-lang-classcastexception-dtoobject-cannot-be-cast-to-dtoobject
        //JdkSerializationRedisSerializer redisSerializer = new JdkSerializationRedisSerializer(getClass().getClassLoader());
        ////设置序列化方式，这里使用JdkSerializationRedisSerializer序列化
        ////这里说明一下，如果使用json方式进行存储，会导致对象中的集合子对象转化出现问题，比如嵌套对象时
        //redisTemplate.setValueSerializer(redisSerializer);
        //redisTemplate.setHashValueSerializer(redisSerializer);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


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

        objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));


        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);


        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    //如果注解式缓存要使用redis，则开启这个bean即可
    @Override
    @Bean
    public CacheManager cacheManager() {
        logger.info("RedisCacheManager");
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate().getConnectionFactory()))
                //缺省配置
                .cacheDefaults(redisCacheConfiguration(3600L))
                //分组配置，不需要分组配置可以去掉，不同的组配置不同的缓存过期时间，可以防止"缓存雪崩"
                .withInitialCacheConfigurations(initialRedisCacheConfiguration())
                .build();
    }

    private RedisCacheConfiguration redisCacheConfiguration(Long ttl) {

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttl)) //设置过期，单位秒
                //.disableCachingNullValues() //不允许存储null值，默认可以存储null，缓存null可以防止"缓存穿透"
                //.disableKeyPrefix()  //设置key前面不带前缀，最好不要去掉前缀，否则执行删除缓存时会清空全部缓存
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate().getStringSerializer()))//key字符串
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate().getValueSerializer()));//value字符串
    }

    /**
     * <h2针对不同的缓存组配置不同的设置</h2>
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

        //这里可以通过配置项进行判断，是否需要抛出异常还是忽略错误，
        // 因为是方法缓存，所以忽略错误并不会导致服务中断，但是会影响服务性能，这里抛出异常
        logger.error("redis异常：key=[{}]", key, exception);
        //throw new CustomException(CustomExceptionType.SYSTEM_ERROR, exception.getMessage());
    }
}

