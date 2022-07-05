package com.example.support;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * <h1>redis cacheEvict aop</h1>
 * Created by hanqf on 2022/5/8 08:19.
 */

//标识是一个Aspect代理类
@Aspect
//如果有多个切面拦截相同的切点，可以用@Order指定执行顺序
//@Order(1)
@Slf4j
public class RedisCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.lexing.common.cache.CP_CacheEvict)")
    public void cacheEvictPointCut() {
    }

    @AfterReturning("cacheEvictPointCut()")
    public void cacheEvictAfterReturning(JoinPoint joinPoint) {
        log.debug("RedisCacheAspect cacheEvictAfterReturning....");

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        CP_CacheEvict annotation = method.getAnnotation(CP_CacheEvict.class);
        String cacheName = annotation.cacheName();
        //没有设置cacheName，则尝试获取类上的配置@CacheConfig(cacheNames = "userDataCache")
        if (!StringUtils.hasText(cacheName)) {
            final CacheConfig cacheConfig = method.getDeclaringClass().getAnnotation(CacheConfig.class);
            if (cacheConfig != null) {
                cacheName = cacheConfig.cacheNames().length > 0 ? cacheConfig.cacheNames()[0] : "";
            }
        }
        String[] keys = annotation.key();
        //转换EL表达式
        cacheName = (String) AspectSupportUtils.getKeyValue(joinPoint, cacheName);

        for (String key : keys) {
            //转换EL表达式
            key = (String) AspectSupportUtils.getKeyValue(joinPoint, key);

            final String cacheNameTemp = cacheName;
            final String keyTemp = key;
            deleteRedisCache(cacheNameTemp, keyTemp);

        }
    }

    private void deleteRedisCache(String key) {
        final Set<String> keys = redisTemplate.keys(key);
        for (String keyName : keys) {
            redisTemplate.delete(keyName);
        }
    }

    private void deleteRedisCache(String cacheName, String key) {
        String redis_key = "";
        redis_key = cacheName + "::" + key;
        deleteRedisCache(redis_key);
    }


}
