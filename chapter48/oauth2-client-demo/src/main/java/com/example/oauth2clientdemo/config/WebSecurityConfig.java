package com.example.oauth2clientdemo.config;

import com.example.oauth2clientdemo.security.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * <h1>WebSecurityConfig</h1>
 * Created by hanqf on 2020/11/7 13:20.
 */


@EnableOAuth2Sso
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Value("${oauth2.server.logout}")
    private String oauth2_server_logout;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //logout跳转到认证服务器的logout
        http.logout().logoutSuccessUrl(oauth2_server_logout);
        http.authorizeRequests()
                //所有路径都需要登录
                .antMatchers("/").authenticated()
                //需要具备相应的角色才能访问
                .antMatchers("/user/**").access("hasRole('admin') or hasRole('user')")
                .anyRequest().access("@rbacService.hasPerssion(request,authentication)");
        ;
        http.csrf().disable();

        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);

    }

    /**
     * 认证事件监听器，打印日志
     * <p>
     * 如：认证失败/成功、注销，等等
     */
    @Bean
    public org.springframework.security.authentication.event.LoggerListener loggerListener() {
        org.springframework.security.authentication.event.LoggerListener loggerListener = new org.springframework.security.authentication.event.LoggerListener();
        return loggerListener;
    }

    /**
     * 资源访问事件监听器，打印日志
     * <p>
     * 如：没有访问权限
     */
    @Bean
    public org.springframework.security.access.event.LoggerListener eventLoggerListener() {
        org.springframework.security.access.event.LoggerListener eventLoggerListener = new org.springframework.security.access.event.LoggerListener();
        return eventLoggerListener;
    }
}