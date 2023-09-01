package com.example.security;

import com.example.mysql.one.repository.SysUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>CustomReactiveUserDetailsService</h1>
 * Created by hanqf on 2023/8/30 17:21.
 */


public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private SysUserRepository sysUserRepository;

    public CustomReactiveUserDetailsService(SysUserRepository sysUserRepository) {
        this.sysUserRepository = sysUserRepository;
    }



    @Override
    public Mono<UserDetails> findByUsername(String username) {
        //封装用户权限，这里只要登录成功就可以访问，所以初始化个空链表
        List<SimpleGrantedAuthority> resultAuths =  new ArrayList<>();
        //将查询到的系统用户转换为UserDetails
        return sysUserRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("UserEntity Not Found")))
                .map(user -> new User(username, user.getPassword(), user.getEnable(), true,
                        true, true, resultAuths));
    }
}
