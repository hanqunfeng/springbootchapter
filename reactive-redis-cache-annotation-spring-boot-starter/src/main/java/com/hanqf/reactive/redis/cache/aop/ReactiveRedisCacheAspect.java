package com.hanqf.reactive.redis.cache.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <h1>redis缓存aop</h1>
 * Created by hanqf on 2020/11/21 16:16.
 */

@Component
//标识是一个Aspect代理类
@Aspect
//如果有多个切面拦截相同的切点，可以用@Order指定执行顺序
//@Order(1)
@Slf4j
public class ReactiveRedisCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.hanqf.reactive.redis.cache.aop.ReactiveRedisCacheable)")
    public void cacheablePointCut() {
    }

    @Pointcut("@annotation(com.hanqf.reactive.redis.cache.aop.ReactiveRedisCacheEvict)")
    public void cacheEvictPointCut() {
    }

    @Pointcut("@annotation(com.hanqf.reactive.redis.cache.aop.ReactiveRedisCachePut)")
    public void cachePutPointCut() {
    }

    @Pointcut("@annotation(com.hanqf.reactive.redis.cache.aop.ReactiveRedisCaching)")
    public void cachingPointCut() {
    }

    //环绕通知,一般不建议使用，可以通过@Before和@AfterReturning实现
    //但是响应式方法只能通过环绕通知实现aop，因为其它通知会导致不再同一个线程执行
    @Around("cacheablePointCut()")
    public Object cacheableAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.debug("ReactiveRedisCacheAspect cacheableAround....");

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String returnTypeName = method.getReturnType().getSimpleName();


        ReactiveRedisCacheable annotation = method.getAnnotation(ReactiveRedisCacheable.class);
        String cacheName = annotation.cacheName();
        String key = annotation.key();
        long timeout = annotation.timeout();

        //转换EL表达式
        cacheName = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, cacheName);
        key = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, key);

        String redis_key = cacheName + "_" + key;

        boolean hasKey = redisTemplate.hasKey(redis_key);
        if (hasKey) {
            Object o = redisTemplate.opsForValue().get(redis_key);
            if (returnTypeName.equals("Flux")) {
                return Flux.fromIterable((List) o);
            } else if (returnTypeName.equals("Mono")) {
                return Mono.just(o);
            } else {
                return o;
            }
        } else {
            //实际执行的方法
            Object proceed = proceedingJoinPoint.proceed();
            if (returnTypeName.equals("Flux")) {
                return ((Flux) proceed).collectList().doOnNext(list -> redisTemplate.opsForValue().set(redis_key, list, timeout, TimeUnit.SECONDS)).flatMapMany(list -> Flux.fromIterable((List) list));
            } else if (returnTypeName.equals("Mono")) {
                return ((Mono) proceed).doOnNext(obj -> redisTemplate.opsForValue().set(redis_key, obj, timeout, TimeUnit.SECONDS));
            } else {
                return proceed;
            }
        }

    }


    @Around("cacheEvictPointCut()")
    public Object cacheEvictAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.debug("ReactiveRedisCacheAspect cacheEvictAround....");

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String returnTypeName = method.getReturnType().getSimpleName();

        ReactiveRedisCacheEvict annotation = method.getAnnotation(ReactiveRedisCacheEvict.class);
        String cacheName = annotation.cacheName();
        String key = annotation.key();
        boolean allEntries = annotation.allEntries();
        boolean beforeInvocation = annotation.beforeInvocation();

        //转换EL表达式
        cacheName = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, cacheName);
        key = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, key);


        //执行方法前清除缓存
        if (beforeInvocation) {

            //清除全部缓存
            deleteRedisCache(cacheName, key, allEntries);

            //实际执行的方法
            Object proceed = proceedingJoinPoint.proceed();
            return proceed;
        } else {//成功执行方法后清除缓存

            //实际执行的方法
            Object proceed = proceedingJoinPoint.proceed();

            final String cacheNameTemp = cacheName;
            final String keyTemp = key;

            if (returnTypeName.equals("Flux")) {
                return ((Flux) proceed).collectList().doOnNext(list -> {
                    //清除全部缓存
                    deleteRedisCache(cacheNameTemp, keyTemp, allEntries);
                }).flatMapMany(list -> Flux.fromIterable((List) list));
            } else if (returnTypeName.equals("Mono")) {
                return ((Mono) proceed).doOnNext(obj -> {
                    //清除全部缓存
                    deleteRedisCache(cacheNameTemp, keyTemp, allEntries);
                });
            } else {
                return proceed;
            }

        }
    }


    @Around("cachePutPointCut()")
    public Object cachePutAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.debug("ReactiveRedisCacheAspect cachePutAround....");

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String returnTypeName = method.getReturnType().getSimpleName();

        ReactiveRedisCachePut annotation = method.getAnnotation(ReactiveRedisCachePut.class);
        String cacheName = annotation.cacheName();
        String key = annotation.key();
        long timeout = annotation.timeout();

        //转换EL表达式
        cacheName = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, cacheName);
        key = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, key);

        String redis_key = cacheName + "_" + key;

        boolean hasKey = redisTemplate.hasKey(redis_key);
        if (hasKey) {
            redisTemplate.delete(redis_key);
        }

        //实际执行的方法
        Object proceed = proceedingJoinPoint.proceed();
        if (returnTypeName.equals("Flux")) {
            return ((Flux) proceed).collectList().doOnNext(list -> redisTemplate.opsForValue().set(redis_key, list, timeout, TimeUnit.SECONDS)).flatMapMany(list -> Flux.fromIterable((List) list));
        } else if (returnTypeName.equals("Mono")) {
            return ((Mono) proceed).doOnNext(obj -> redisTemplate.opsForValue().set(redis_key, obj, timeout, TimeUnit.SECONDS));
        } else {
            return proceed;
        }
    }


    @Around("cachingPointCut()")
    public Object cachingAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.debug("ReactiveRedisCacheAspect cachingAround....");

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String returnTypeName = method.getReturnType().getSimpleName();

        ReactiveRedisCaching annotation = method.getAnnotation(ReactiveRedisCaching.class);

        ReactiveRedisCacheEvict[] cacheEvicts = annotation.evict();
        ReactiveRedisCachePut[] cachePuts = annotation.put();
        ReactiveRedisCacheable[] cacheables = annotation.cacheable();

        //规则：
        //1.cacheables不能与cacheEvicts或者cachePuts同时存在，因为后者一定会执行方法主体，达不到调用缓存的目的，所以当cacheables存在时，后者即便指定也不执行
        //2.先执行cacheEvicts，再执行cachePuts

        if (cacheables.length > 0) {
            Map<String, Long> key_map = new HashMap<>();
            List<String> key_list = new ArrayList<>();
            Arrays.stream(cacheables).forEach(cacheable -> {
                String cacheName = cacheable.cacheName();
                String key = cacheable.key();
                long timeout = cacheable.timeout();

                //转换EL表达式
                cacheName = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, cacheName);
                key = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, key);

                String redis_key = cacheName + "_" + key;

                key_map.put(redis_key, timeout);
                key_list.add(redis_key);
            });

            AtomicBoolean isAllKeyHas = new AtomicBoolean(true);
            key_list.forEach(key -> {
                if (!redisTemplate.hasKey(key)) {
                    isAllKeyHas.set(false);
                }
            });

            //全部key都有值，则直接返回缓存
            if (isAllKeyHas.get()) {
                Object o = redisTemplate.opsForValue().get(key_list.get(0));
                if (returnTypeName.equals("Flux")) {
                    return Flux.fromIterable((List) o);
                } else if (returnTypeName.equals("Mono")) {
                    return Mono.just(o);
                } else {
                    return o;
                }
            } else {
                //实际执行的方法
                Object proceed = proceedingJoinPoint.proceed();

                if (returnTypeName.equals("Flux")) {
                    return ((Flux) proceed).collectList()
                            .doOnNext(list -> key_map.forEach((key, val) -> redisTemplate.opsForValue().set(key, list, val, TimeUnit.SECONDS)))
                            .flatMapMany(list -> Flux.fromIterable((List) list));
                } else if (returnTypeName.equals("Mono")) {
                    return ((Mono) proceed)
                            .doOnNext(obj -> key_map.forEach((key, val) -> redisTemplate.opsForValue().set(key, obj, val, TimeUnit.SECONDS)));
                } else {
                    return proceed;
                }
            }

        } else {


            Map<String, Boolean> map = new HashMap<>();
            if (cacheEvicts.length > 0) {
                Arrays.stream(cacheEvicts).forEach(cacheEvict -> {
                    String cacheName = cacheEvict.cacheName();
                    String key = cacheEvict.key();
                    boolean allEntries = cacheEvict.allEntries();
                    boolean beforeInvocation = cacheEvict.beforeInvocation();

                    //转换EL表达式
                    cacheName = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, cacheName);
                    key = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, key);

                    if (beforeInvocation) { //执行方法前清除缓存
                        //清除全部缓存
                        deleteRedisCache(cacheName, key, allEntries);
                    } else { //成功执行方法后清除缓存，先保存到map中
                        //清除全部缓存
                        if (allEntries) {
                            map.put(cacheName, true);
                        } else {
                            map.put(cacheName + "_" + key, false);
                        }
                    }
                });
            }

            //实际执行的方法
            Object proceed = proceedingJoinPoint.proceed();


            if (cachePuts.length > 0) {
                Map<String, Long> key_map = new HashMap<>();
                Arrays.stream(cachePuts).forEach(cachePut -> {
                    String cacheName = cachePut.cacheName();
                    String key = cachePut.key();
                    long timeout = cachePut.timeout();

                    //转换EL表达式
                    cacheName = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, cacheName);
                    key = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, key);

                    String redis_key = cacheName + "_" + key;

                    key_map.put(redis_key, timeout);

                    boolean hasKey = redisTemplate.hasKey(redis_key);
                    if (hasKey) {
                        redisTemplate.delete(redis_key);
                    }

                });

                if (returnTypeName.equals("Flux")) {
                    return ((Flux) proceed).collectList()
                            .doOnNext(list -> {
                                //执行方法后清除缓存
                                if (map.size() > 0) {
                                    map.forEach((key, val) -> {
                                        deleteRedisCache(key, val);
                                    });
                                }
                                key_map.forEach((key, val) -> redisTemplate.opsForValue().set(key, list, val, TimeUnit.SECONDS));
                            })
                            .flatMapMany(list -> Flux.fromIterable((List) list));
                } else if (returnTypeName.equals("Mono")) {
                    return ((Mono) proceed)
                            .doOnNext(obj -> {
                                //执行方法后清除缓存
                                if (map.size() > 0) {
                                    map.forEach((key, val) -> {
                                        deleteRedisCache(key, val);
                                    });
                                }
                                key_map.forEach((key, val) -> redisTemplate.opsForValue().set(key, obj, val, TimeUnit.SECONDS));
                            });
                } else {
                    return proceed;
                }
            } else {

                if (returnTypeName.equals("Flux")) {
                    return ((Flux) proceed).collectList().doOnNext(list -> {
                        //执行方法后清除缓存
                        if (map.size() > 0) {
                            map.forEach((key, val) -> {
                                deleteRedisCache(key, val);
                            });
                        }
                    }).flatMapMany(list -> Flux.fromIterable((List) list));
                } else if (returnTypeName.equals("Mono")) {
                    return ((Mono) proceed).doOnNext(obj -> {
                        //执行方法后清除缓存
                        if (map.size() > 0) {
                            map.forEach((key, val) -> {
                                deleteRedisCache(key, val);
                            });
                        }
                    });
                } else {
                    return proceed;
                }
            }
        }


    }

    private void deleteRedisCache(String key, boolean clearAll) {
        if (clearAll) {
            Set keys = redisTemplate.keys(key + "_*");
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } else {
            if (redisTemplate.hasKey(key)) {
                redisTemplate.delete(key);
            }
        }
    }

    private void deleteRedisCache(String cacheName, String key, boolean clearAll) {

        String redis_key = "";
        if (clearAll) {
            redis_key = cacheName + "_*";
        } else {
            redis_key = cacheName + "_" + key;
        }

        deleteRedisCache(redis_key, clearAll);
    }

}
