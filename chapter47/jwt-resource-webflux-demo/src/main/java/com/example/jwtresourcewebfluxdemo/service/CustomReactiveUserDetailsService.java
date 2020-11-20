package com.example.jwtresourcewebfluxdemo.service;

import com.example.jwtresourcewebfluxdemo.dao.SysUserRepository;
import com.example.jwtresourcewebfluxdemo.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <h1>ReactiveUserDetailsService</h1>
 * Created by hanqf on 2020/11/19 10:06.
 */
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 参考：https://www.jdon.com/52285
     * 自Spring 5.2 M2之后，Spring开始支持反应式事务
     * 所有事务管理都在幕后进行，利用Spring的事务拦截器和ReactiveTransactionManager。
     * <p>
     * Spring基于方法返回类型分辨要应用的事务管理类型：
     * <p>
     * 方法返回一个Publisher类型：响应式事务管理
     * 所有其他return类型：传统的命令式事务管理
     */
    @Transactional(readOnly = true)
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        //封装用户权限，这里只要登录成功就可以访问，所以初始化个空链表
        List<SimpleGrantedAuthority> resultAuths = new ArrayList<>();
        //将查询到的系统用户转换为UserDetails
        return sysUserRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User Not Found")))
                .map(user -> new User(username, user.getPassword(), user.getEnable(), true,
                        true, true, resultAuths));
    }

    @Transactional(readOnly = true)
    public Mono<SysUser> findUserByUsername(String username) {
        String key = "sysuser_" + username;
        ValueOperations<String, SysUser> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            SysUser sysUser = operations.get(key);
            return Mono.just(sysUser);
        } else {
            return sysUserRepository.findByUsername(username).doOnNext(user -> operations.set(key, user));
        }
    }


    @Transactional(rollbackFor = {Throwable.class})
    public Mono<SysUser> add(SysUser sysUser) {
        //清空缓存
        Set sysuser_ = redisTemplate.keys("sysuser_*");
        redisTemplate.delete(sysuser_);
        return sysUserRepository.addSysUser(sysUser.getId(), sysUser.getUsername(), sysUser.getPassword(), sysUser.getEnable()).flatMap(data -> sysUserRepository.findById(sysUser.getId()));
    }

    @Transactional(rollbackFor = {Throwable.class})
    public Mono<SysUser> update(SysUser sysUser) {
        //清空缓存
        Set sysuser_ = redisTemplate.keys("sysuser_*");
        redisTemplate.delete(sysuser_);
        Mono<SysUser> save = sysUserRepository.save(sysUser);
        return save;
    }

    @Transactional(readOnly = true)
    public Flux<SysUser> findAll() {
        String key = "sysuser_all";
        ValueOperations<String, List<SysUser>> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<SysUser> sysUsers = operations.get(key);
            return Flux.fromIterable(sysUsers);
        } else {
            return sysUserRepository.findAll().collectList().doOnNext(list -> operations.set(key, list)).flatMapMany(list -> Flux.fromIterable(list));
        }
    }

    @Transactional(rollbackFor = {Throwable.class})
    public Mono<Boolean> deleteByUserName(String username) {
        //清空缓存
        Set sysuser_ = redisTemplate.keys("sysuser_*");
        redisTemplate.delete(sysuser_);
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


