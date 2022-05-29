package com.hanqf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * RedisSessionConfig
 */
@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {
}
