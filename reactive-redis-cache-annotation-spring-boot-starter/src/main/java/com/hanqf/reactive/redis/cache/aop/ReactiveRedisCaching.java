package com.hanqf.reactive.redis.cache.aop;

import java.lang.annotation.*;

/**
 * <h1>组合</h1>
 * Created by hanqf on 2020/11/21 23:24.
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReactiveRedisCaching {

    ReactiveRedisCacheable[] cacheable() default {};

    ReactiveRedisCachePut[] put() default {};

    ReactiveRedisCacheEvict[] evict() default {};
}
