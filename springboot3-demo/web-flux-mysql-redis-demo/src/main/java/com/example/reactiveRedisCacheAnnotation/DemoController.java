package com.example.reactiveRedisCacheAnnotation;

import com.example.mysql.SysUser;
import com.example.mysql.SysUserRepository;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheEvict;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCachePut;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCaching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <h1>Reactive Redis Cache Annotation Demo</h1>
 * Created by hanqf on 2023/9/11 16:20.
 */

@RestController
@RequestMapping("/cache")
public class DemoController {

    @Autowired
    private SysUserRepository sysUserRepository;
    @ReactiveRedisCacheable(cacheName = "sys-user", key = "all")
    @RequestMapping("/all")
    public Flux<SysUser> findAll() {
        return sysUserRepository.findAll();
    }



    @ReactiveRedisCacheable(cacheName = "sys-user", key = "'find_' + #username")
    @RequestMapping("/find/{username}")
    public Mono<SysUser> findUserByUsername(@PathVariable String username) {
        return sysUserRepository.findByUsername(username);
    }

    /**
     * 删除指定的"cacheName:key"
     */
    @ReactiveRedisCacheEvict(cacheName = "sys-user", key = "'find_' + #username")
    @RequestMapping("/delete/{username}")
    public Mono<Boolean> deleteByUserName(@PathVariable String username) {
        return sysUserRepository.deleteByUsername(username);
    }


    /**
     * 删除缓存，allEntries = true 表示删除全部以"cacheName:"开头的缓存
     * allEntries 默认false，此时需要指定key的值，表示删除指定的"cacheName:key"
     */
    @ReactiveRedisCacheEvict(cacheName = "sys-user", allEntries = true)
    @RequestMapping("/save")
    public Mono<SysUser> save(@RequestBody SysUser sysUser) {
        return sysUserRepository.addSysUser(sysUser)
                .flatMap(e -> sysUserRepository.findById(sysUser.getId()));
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
    @RequestMapping("/update")
    public Mono<SysUser> update(@RequestBody SysUser sysUser) {
        return sysUserRepository.save(sysUser);
    }

}
