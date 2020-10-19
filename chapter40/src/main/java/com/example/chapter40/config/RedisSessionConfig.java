package com.example.chapter40.config;/**
 * Created by hanqf on 2020/3/23 15:38.
 */


import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author hanqf
 * @date 2020/3/23 15:38
 */
@Configuration
//设置session的过期时间为60分钟
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 60)
public class RedisSessionConfig {

}
