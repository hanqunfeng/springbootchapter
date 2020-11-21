package com.example.jwtresourcewebfluxdemo.aop;

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
public class RedisCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    //切点
    //execution 表示在执行的时候，拦截里面的正则匹配的方法
    // *.*(..) : 表示任意类的任意方法，第一个*代表类名为任何名称，第二个*代表方法返回类型为任意类型，(..)代表方法的任意参数
    //@Pointcut("execution(* com.example.jwtresourcewebfluxdemo.service.*.*(..))")
    @Pointcut("@annotation(com.example.jwtresourcewebfluxdemo.aop.RedisCacheable)")
    public void cacheablePointCut() {
    }

    @Pointcut("@annotation(com.example.jwtresourcewebfluxdemo.aop.RedisCacheEvict)")
    public void cacheEvictPointCut() {
    }

    @Pointcut("@annotation(com.example.jwtresourcewebfluxdemo.aop.RedisCachePut)")
    public void cachePutPointCut() {
    }

    @Pointcut("@annotation(com.example.jwtresourcewebfluxdemo.aop.RedisCaching)")
    public void cachingPointCut() {
    }

    //环绕通知,一般不建议使用，可以通过@Before和@AfterReturning实现
    //但是响应式方法只能通过环绕通知实现aop，因为其它通知会导致不再同一个线程执行
    @Around("cacheablePointCut()")
    public Object cacheableAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("cacheableAround....");
        //System.out.println(proceedingJoinPoint.getArgs());
        //System.out.println(proceedingJoinPoint.getTarget()); //目标对象

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String returnTypeName = method.getReturnType().getSimpleName();

        //Arrays.stream(method.getDeclaredAnnotations()).forEach(System.out::println);

        RedisCacheable annotation = method.getAnnotation(RedisCacheable.class);
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
        log.info("cacheEvictAround....");

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();

        RedisCacheEvict annotation = method.getAnnotation(RedisCacheEvict.class);
        String cacheName = annotation.cacheName();
        String key = annotation.key();
        boolean allEntries = annotation.allEntries();

        //转换EL表达式
        cacheName = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, cacheName);
        key = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, key);

        //实际执行的方法
        Object proceed = proceedingJoinPoint.proceed();

        //清除全部缓存
        if (allEntries) {
            Set keys = redisTemplate.keys(cacheName + "_*");
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } else {
            String redis_key = cacheName + "_" + key;
            if (redisTemplate.hasKey(redis_key)) {
                redisTemplate.delete(redis_key);
            }
        }

        return proceed;
    }


    @Around("cachePutPointCut()")
    public Object cachePutAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("cachePutAround....");

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String returnTypeName = method.getReturnType().getSimpleName();

        RedisCachePut annotation = method.getAnnotation(RedisCachePut.class);
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
        log.info("cachingAround....");

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String returnTypeName = method.getReturnType().getSimpleName();

        RedisCaching annotation = method.getAnnotation(RedisCaching.class);

        RedisCacheEvict[] cacheEvicts = annotation.evict();
        RedisCachePut[] cachePuts = annotation.put();
        RedisCacheable[] cacheables = annotation.cacheable();

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
            //实际执行的方法
            Object proceed = proceedingJoinPoint.proceed();

            if (cacheEvicts.length > 0) {
                Arrays.stream(cacheEvicts).forEach(cacheEvict -> {
                    String cacheName = cacheEvict.cacheName();
                    String key = cacheEvict.key();
                    boolean allEntries = cacheEvict.allEntries();

                    //转换EL表达式
                    cacheName = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, cacheName);
                    key = (String) AspectSupportUtils.getKeyValue(proceedingJoinPoint, key);

                    //清除全部缓存
                    if (allEntries) {
                        Set keys = redisTemplate.keys(cacheName + "_*");
                        if (!keys.isEmpty()) {
                            redisTemplate.delete(keys);
                        }
                    } else {
                        String redis_key = cacheName + "_" + key;
                        if (redisTemplate.hasKey(redis_key)) {
                            redisTemplate.delete(redis_key);
                        }
                    }
                });
            }

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
                            .doOnNext(list -> key_map.forEach((key, val) -> redisTemplate.opsForValue().set(key, list, val, TimeUnit.SECONDS)))
                            .flatMapMany(list -> Flux.fromIterable((List) list));
                } else if (returnTypeName.equals("Mono")) {
                    return ((Mono) proceed)
                            .doOnNext(obj -> key_map.forEach((key, val) -> redisTemplate.opsForValue().set(key, obj, val, TimeUnit.SECONDS)));
                } else {
                    return proceed;
                }
            }

            return proceed;
        }


    }

}
