package com.example.springsecuritydemo.common;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>自定义认证用户model</p>
 * Created by hanqf on 2020/9/24 11:12.
 */


public class CustomerUser extends User {

    /**
     * 存储服务端返回属性
    */
    private Map<String,Object> map = new HashMap<>();

    public Map<String, Object> getMap() {
        return map;
    }

    public CustomerUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Map<String, Object> map) {
        super(username, password, authorities);
        this.map = map;
    }

    public CustomerUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Map<String, Object> map) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.map = map;
    }
}
