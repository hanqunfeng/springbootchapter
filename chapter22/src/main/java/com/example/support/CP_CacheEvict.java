package com.example.support;

import java.lang.annotation.*;

/**
 * <h1>redis清除缓存注解--支持模糊匹配</h1>
 * Created by hanqf on 2022/5/8 08:09.
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CP_CacheEvict {
    /**
     * 缓存key，如果cacheName不为空，则key为cacheName+"::"+key
     * 支持EL表达式
     */
    String[] key() default {};

    /**
     * 缓存key分组，会做为缓存key的前缀+"::"
     * 支持EL表达式
     */
    String cacheName() default "";

}
