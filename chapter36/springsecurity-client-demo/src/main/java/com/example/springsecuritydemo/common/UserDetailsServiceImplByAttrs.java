package com.example.springsecuritydemo.common;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * <p>一个UserDetailsService的实现demo</p>
 * Created by hanqf on 2020/9/10 14:08.
 */

public class UserDetailsServiceImplByAttrs extends AbstractCasAssertionUserDetailsService {

    private static final String NON_EXISTENT_PASSWORD_VALUE = "NO_PASSWORD";
    //设置接收哪些属性，默认null，表示全部接收
    private String[] attributes = null;

    public UserDetailsServiceImplByAttrs() {
    }

    public UserDetailsServiceImplByAttrs(String[] attributes) {
        this.attributes = attributes;
    }

    public String[] getAttributes() {
        return attributes;
    }

    /***
     * 验证用户，正式使用一般查询数据库进行比对
     * @param assertion
     * @return
     */
    @Override
    protected UserDetails loadUserDetails(Assertion assertion) {

        String username = assertion.getPrincipal().getName();
        //此处可以判断用户是否存在
        //............

        //客户端想接收的属性
        Map<String, Object> attrsMap = new HashMap<>();
        //实际可以接收的属性
        Map<String, Object> objectMap = assertion.getPrincipal().getAttributes();
        //cas-server返回属性处理
        if (attributes == null) {
            attrsMap = objectMap;
        } else {
            for (String key : attributes) {
                attrsMap.put(key, objectMap.get(key));
            }
        }
        //打印属性测试
        for (String key : objectMap.keySet()) {
            Object value = objectMap.get(key);
            if (value != null) {
                if (value instanceof List) {
                    List list = (List) value;
                    Iterator iterator = list.iterator();
                    int i = 0;
                    while (iterator.hasNext()) {
                        Object object = iterator.next();
                        System.out.println("key:" + key + ",values[" + i + "]:" + object.toString());
                        i++;
                    }
                } else {
                    System.out.println("key:" + key + ",value:" + value.toString());
                }
            }
        }


        //角色信息
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        //返回自定义用户对象
        return new CustomerUser(username, NON_EXISTENT_PASSWORD_VALUE, grantedAuthorities, attrsMap);
    }
}
