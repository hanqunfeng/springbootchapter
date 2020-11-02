package com.example.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * <h1>SecuritySecureConfig</h1>
 * Created by hanqf on 2020/11/2 13:57.
 * <p>
 * 这里只对endpoint路径进行拦截，其余全部放行
 * <p>
 * 参考：https://www.jianshu.com/p/25d5a85ce8dd?utm_campaign=maleskine&utm_content=note&utm_medium=seo_notes&utm_source=recommendation
 */

@Configuration
public class SecuritySecureConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //拦截所有endpoint，拥有ACTUATOR_ADMIN角色可访问，否则需登录
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR_ADMIN")
                //静态文件允许访问
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                //根路径允许访问
                .antMatchers("/").permitAll()
                //所有请求路径可以访问
                .antMatchers("/**").permitAll()
                .and().formLogin()
                .and().httpBasic();
    }
}