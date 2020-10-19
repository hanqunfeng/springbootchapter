package com.example.chapter40.config;/**
 * Created by hanqf on 2020/3/23 15:38.
 */


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author hanqf
 * @date 2020/3/23 15:38
 */
@Configuration
//设置session的过期时间为60分钟
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 60)
@Slf4j
public class RedisSessionConfig {

    /**
     * <h2>设置session值存储方式，默认jdk序列化方式，这里改为json</h2>
     * Created by hanqf on 2020/10/19 18:07. <br>
     *
     * @return org.springframework.data.redis.serializer.RedisSerializer&lt;java.lang.Object&gt;
     * @author hanqf
     */
    @Bean("springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> defaultRedisSerializer() {
        log.debug("自定义Redis Session序列化加载成功");
        return new GenericJackson2JsonRedisSerializer();
    }

}
