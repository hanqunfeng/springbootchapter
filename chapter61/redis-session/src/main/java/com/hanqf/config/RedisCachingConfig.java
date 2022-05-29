package com.hanqf.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author hanqf
 */
@Configuration
@ConditionalOnProperty(prefix = "redis.cache", name = "enabled", havingValue = "true")
@PropertySource(name = "redisCache", value = "classpath*:application-redis-${spring.profiles.active}.properties", factory = CP_PropertySourceFactory.class)
//注入redis分组配置属性：ttlmap
@ConfigurationProperties(prefix = "caching")
public class RedisCachingConfig extends BaseRedisCachingConfig {
}
