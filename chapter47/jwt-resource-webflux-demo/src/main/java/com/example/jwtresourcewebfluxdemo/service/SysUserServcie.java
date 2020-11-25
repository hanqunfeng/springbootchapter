package com.example.jwtresourcewebfluxdemo.service;

import com.example.jwtresourcewebfluxdemo.dao.SysUserRepository;
import com.example.jwtresourcewebfluxdemo.model.SysUser;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheEvict;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCachePut;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCaching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <h1>SysUserService</h1>
 * Created by hanqf on 2020/11/23 11:12.
 */

@Service
public class SysUserServcie {

    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    private RedisTemplate redisTemplate;


    //@RedisCacheable(cacheName = "sysuser", key = "'find_' + #username")
    @ReactiveRedisCacheable(cacheName = "sysuser", key = "'find_' + #username")
    @Transactional(readOnly = true)
    public Mono<SysUser> findUserByUsername(String username) {
        //String key = "sysuser_" + username;
        //ValueOperations<String, SysUser> operations = redisTemplate.opsForValue();
        //boolean hasKey = redisTemplate.hasKey(key);
        //if (hasKey) {
        //    SysUser sysUser = operations.get(key);
        //    return Mono.just(sysUser);
        //} else {
        //    return sysUserRepository.findByUsername(username).doOnNext(user -> operations.set(key, user));
        //}
        return sysUserRepository.findByUsername(username);
    }


    @ReactiveRedisCacheEvict(cacheName = "sysuser", allEntries = true)
    @Transactional(rollbackFor = {Throwable.class})
    public Mono<SysUser> add(SysUser sysUser) {
        //清空缓存
        //Set sysuser_ = redisTemplate.keys("sysuser_*");
        //redisTemplate.delete(sysuser_);
        return sysUserRepository.addSysUser(sysUser.getId(), sysUser.getUsername(), sysUser.getPassword(), sysUser.getEnable()).flatMap(data -> sysUserRepository.findById(sysUser.getId()));
    }

    //@RedisCacheEvict(cacheName = "sysuser",allEntries = true)
    @ReactiveRedisCaching(
            evict = {@ReactiveRedisCacheEvict(cacheName = "sysuser", key = "all")},
            put = {@ReactiveRedisCachePut(cacheName = "sysuser", key = "'find_' + #sysUser.username")}
    )
    @Transactional(rollbackFor = {Throwable.class})
    public Mono<SysUser> update(SysUser sysUser) {
        //清空缓存
        //Set sysuser_ = redisTemplate.keys("sysuser_*");
        //redisTemplate.delete(sysuser_);
        Mono<SysUser> save =sysUserRepository.save(sysUser);
        return save;
    }

    @ReactiveRedisCacheable(cacheName = "sysuser", key = "all")
    @Transactional(readOnly = true)
    public Flux<SysUser> findAll() {
        //String key = "sysuser_all";
        //ValueOperations<String, List<SysUser>> operations = redisTemplate.opsForValue();
        //boolean hasKey = redisTemplate.hasKey(key);
        //if (hasKey) {
        //    List<SysUser> sysUsers = operations.get(key);
        //    return Flux.fromIterable(sysUsers);
        //} else {
        //    return sysUserRepository.findAll().collectList().doOnNext(list -> operations.set(key, list)).flatMapMany(list -> Flux.fromIterable(list));
        //}

        return sysUserRepository.findAll();
    }

    @ReactiveRedisCacheEvict(cacheName = "sysuser", allEntries = true)
    @Transactional(rollbackFor = {Throwable.class})
    public Mono<Boolean> deleteByUserName(String username) {
        //清空缓存
        //Set sysuser_ = redisTemplate.keys("sysuser_*");
        //redisTemplate.delete(sysuser_);
        return sysUserRepository.deleteByUsername(username);
    }

    @Transactional(readOnly = true)
    public Flux<SysUser> findAllByPage(Integer page, Integer size) {
        return sysUserRepository.findAllByPage(page * size, size);
    }

    @Transactional(readOnly = true)
    public Flux<SysUser> findAllBySort() {
        return sysUserRepository.findAll(Sort.by(Sort.Direction.DESC, "username"));
    }
}
