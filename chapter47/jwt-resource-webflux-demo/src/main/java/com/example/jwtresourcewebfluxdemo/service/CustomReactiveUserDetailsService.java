package com.example.jwtresourcewebfluxdemo.service;

import com.example.jwtresourcewebfluxdemo.model.SysUser;
import com.example.jwtresourcewebfluxdemo.dao.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * <h1>ReactiveUserDetailsService</h1>
 * Created by hanqf on 2020/11/19 10:06.
 */


public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    SysUserRepository sysUserRepository;


    /**
     * 参考：https://www.jdon.com/52285
     * 自Spring 5.2 M2之后，Spring开始支持反应式事务
     * 所有事务管理都在幕后进行，利用Spring的事务拦截器和ReactiveTransactionManager。
     *
     * Spring基于方法返回类型分辨要应用的事务管理类型：
     *
     * 方法返回一个Publisher类型：响应式事务管理
     * 所有其他return类型：传统的命令式事务管理
    */
    @Transactional(readOnly = true)
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        //封装用户权限，这里只要登录成功就可以访问，所以初始化个空链表
        List<SimpleGrantedAuthority> resultAuths =  new ArrayList<>();
        //将查询到的系统用户转换为UserDetails
        return sysUserRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User Not Found")))
                .map(user -> new User(username, user.getPassword(), user.getEnable(), true,
                        true, true, resultAuths));
    }

    @Transactional(readOnly = true)
    public Mono<SysUser> findUserByUsername(String username) {
        return sysUserRepository.findByUsername(username);
    }



    @Transactional(rollbackFor = {Throwable.class})
    public Mono<SysUser> add(SysUser sysUser){
        return sysUserRepository.addSysUser(sysUser.getId(), sysUser.getUsername(), sysUser.getPassword(), sysUser.getEnable()).flatMap(data -> sysUserRepository.findById(sysUser.getId()));
    }

    @Transactional(rollbackFor = {Throwable.class})
    public Mono<SysUser> update(SysUser sysUser){

        Mono<SysUser> save = sysUserRepository.save(sysUser);
        return save;
    }

    @Transactional(readOnly = true)
    public Flux<SysUser> findAll(){
        return sysUserRepository.findAll();
    }

    @Transactional(rollbackFor = {Throwable.class})
    public Mono<Boolean> deleteByUserName(String username){
        return sysUserRepository.deleteByUsername(username);
    }
}


