package com.example.jwtresourcewebfluxdemo.aop;

import java.lang.annotation.*;

/**
 * <h1>redis清除缓存注解</h1>
 * Created by hanqf on 2020/11/21 22:26.
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCacheEvict {
    /**
     * 缓存key，如果cacheName不为空，则key为cacheName+"_"+key
     * 支持EL表达式
     */
    String key() default "";

    /**
     * 缓存key分组，会做为缓存key的前缀+"_"
     * 支持EL表达式
     */
    String cacheName();

    /**
     * 是否删除cacheName下全部缓存数据，
     * true时cacheName不能为空，此时即便指定了key值，也会删除cacheName下全部缓存
     * false时key值不能为空
     */
    boolean allEntries() default false;

    /**
     * 调用清除缓存的时机，true:执行方法前，false:执行方法后
     * 如果是false，则方法执行过程中发生异常，则不会清除缓存
    */
    boolean beforeInvocation() default false;
}
