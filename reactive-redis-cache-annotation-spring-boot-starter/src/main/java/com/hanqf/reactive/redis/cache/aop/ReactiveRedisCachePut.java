package com.hanqf.reactive.redis.cache.aop;

import java.lang.annotation.*;

/**
 * <h1>执行完方法更新缓存</h1>
 * Created by hanqf on 2020/11/21 23:15.
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReactiveRedisCachePut {

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
