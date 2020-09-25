package com.example.springsecuritydemo.common;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>一个UserDetailsService的实现demo</p>
 * Created by hanqf on 2020/9/10 14:08.
 */

public class UserDetailsServiceImpl implements UserDetailsService {

    /***
     * 验证用户，正式使用一般查询数据库进行比对
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password="123456";

        //角色信息
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_admin"));

        return new User(username,password,grantedAuthorities);
    }
}
