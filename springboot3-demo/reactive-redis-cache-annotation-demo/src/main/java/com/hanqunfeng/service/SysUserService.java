package com.hanqunfeng.service;

import com.hanqunfeng.model.SysUser;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheEvict;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCachePut;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCaching;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>Demo Service</h1>
 * Created by hanqf on 2024/7/18 10:05.
 */
@Service
public class SysUserService {

    private static Map<String, SysUser> sysUserMap = new ConcurrentHashMap<>();

    static {
        SysUser sysUser1 = new SysUser();
        sysUser1.setId("5c74a4e4-c4f2-4570-8735-761d7a570d36");
        sysUser1.setUsername("lisi");
        sysUser1.setPassword("$2a$10$PXoGXLwg05.5YO.QtZa46ONypBmiK59yfefvO1OGO8kYFwzOB.Os6");
        sysUser1.setEnable(true);
        SysUser sysUser2 = new SysUser();
        sysUser2.setId("5c74a4e4-c4f2-4570-8735-88888888");
        sysUser2.setUsername("zhangsan");
        sysUser2.setPassword("$2a$10$PXoGXLwg05.5YO.QtZa46ONypBmiK59yfefvO1OGO8kYFwzOB.Os6");
        sysUser2.setEnable(true);
        sysUserMap.put(sysUser1.getUsername(), sysUser1);
        sysUserMap.put(sysUser2.getUsername(), sysUser2);
    }

    /**
     * 缓存 cacheName和key支持EL表达式，实际key的名称是"cacheName:key"
     * 缓存结果
     * key:sys-user:find_lisi
     * value:
     * [
     * "com.hanqunfeng.model.SysUser"
     * {
     * id:"5c74a4e4-c4f2-4570-8735-761d7a570d36"
     * username:"lisi"
     * password:"$2a$10$PXoGXLwg05.5YO.QtZa46ONypBmiK59yfefvO1OGO8kYFwzOB.Os6"
     * enable:true
     * }
     * ]
     */
    @ReactiveRedisCacheable(cacheName = "sys-user", key = "'find_' + #username")
    public Mono<SysUser> findUserByUsername(String username) {
//        return Mono.just(sysUserMap.get(username));
//        return Mono.justOrEmpty(null);
        return Mono.justOrEmpty(sysUserMap.get(username));
    }


    @ReactiveRedisCacheable(cacheName = "sys-user", key = "all", timeout = -1, cacheNull = true, cacheNullTimeout = 300)
    public Flux<SysUser> findAll() {
        return Flux.fromIterable(sysUserMap.values());
//        return Flux.empty();
    }

    /**
     * 删除缓存，allEntries = true 表示删除全部以"cacheName:"开头的缓存
     * allEntries 默认false，此时需要指定key的值，表示删除指定的"cacheName:key"
     */
    @ReactiveRedisCacheEvict(cacheName = "sys-user", allEntries = true)
    public Mono<SysUser> add(SysUser sysUser) {
        sysUserMap.put(sysUser.getUsername(), sysUser);
        return Mono.just(sysUser);
    }

    /**
     * 组合注解，用法与@Caching类似
     * 规则：
     * 1.cacheables不能与cacheEvicts或者cachePuts同时存在，因为后者一定会执行方法主体，达不到调用缓存的目的，所以当cacheables存在时，后者即便指定也不执行
     * 2.先执行cacheEvicts，再执行cachePuts
     */
    @ReactiveRedisCaching(
            evict = {@ReactiveRedisCacheEvict(cacheName = "sys-user", key = "all")},
            put = {@ReactiveRedisCachePut(cacheName = "sys-user", key = "'find_' + #sysUser.username")}
    )
    public Mono<SysUser> update(SysUser sysUser) {
        sysUserMap.put(sysUser.getUsername(), sysUser);
        return Mono.just(sysUser);
    }

    /**
     * 删除指定的"cacheName:key"
     */
    @ReactiveRedisCacheEvict(cacheName = "sys-user", key = "'find_' + #username")
    public Mono<Boolean> deleteByUserName(String username) {
        sysUserMap.remove(username);
        return Mono.just(true);
    }

    @ReactiveRedisCacheEvict(cacheName = "sys-user", keys={
            "'find_*'",
            "'all'"
    })
    public Mono<Boolean> deleteAll() {
        sysUserMap.clear();
        return Mono.just(true);
    }

    @ReactiveRedisCaching(
            evict = {@ReactiveRedisCacheEvict(cacheName = "sys-user", keys={
                    "'find_*'",
                    "'all'"
            })}
    )
    public Mono<Boolean> deleteAll2() {
        sysUserMap.clear();
        return Mono.just(true);
    }
}
