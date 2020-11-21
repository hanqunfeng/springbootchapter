package com.example.jwtresourcewebfluxdemo.aop;

import java.lang.annotation.*;

/**
 * <h1>redis方法缓存注解</h1>
 * Created by hanqf on 2020/11/21 18:28.
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCacheable {
    /**
     * 缓存key，key为cacheName+"_"+key
     * 支持EL表达式
    */
    String key();

    /**
     * 缓存key分组，会做为缓存key的前缀+"_"
     * 支持EL表达式
    */
    String cacheName();

    /**
     * 缓存过期时间，单位秒，默认24小时
    */
    long timeout() default 24 * 3600L;
}
