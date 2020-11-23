package com.example.jwtresourcewebfluxdemo.config;

import com.example.jwtresourcewebfluxdemo.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * <h1>安全认证配置</h1>
 * Created by hanqf on 2020/11/19 10:26.
 */


@Configuration
//@EnableWebFluxSecurity //非必要
@EnableReactiveMethodSecurity //启用@PreAuthorize注解配置
public class ReactiveSecurityConfig {


    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private JwtSecurityContextRepository jwtSecurityContextRepository;

    @Autowired
    private CustomServerAccessDeniedHandler customServerAccessDeniedHandler;

    @Autowired
    private CustomServerAuthenticationEntryPoint customServerAuthenticationEntryPoint;
    /**
     * 注册安全验证规则
     * 配置方式与HttpSecurity基本一致
    */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http){ //定义SecurityWebFilterChain对安全进行控制，使用ServerHttpSecurity构造过滤器链；
        return http.authorizeExchange()
                .anyExchange().authenticated() //所有请求都需要通过认证
                .and()
                .csrf().disable() //关闭CSRF（Cross-site request forgery）跨站请求伪造
                .httpBasic().disable() //不支持HTTP Basic方式登录
                .formLogin().disable()//不支持login页面登录
                .addFilterAfter(jwtAuthenticationTokenFilter, SecurityWebFiltersOrder.FIRST)
                .securityContextRepository(jwtSecurityContextRepository)
                .exceptionHandling().accessDeniedHandler(customServerAccessDeniedHandler)
                .authenticationEntryPoint(customServerAuthenticationEntryPoint)
                .and()
                .build();
    }


    /**
     * 注册UserDetailsService
     *
     * 这里为了测试响应式mysql，所以使用了自定义ReactiveUserDetailsService的方式
    */
    @Bean
    ReactiveUserDetailsService reactiveUserDetailsService(){
        return new CustomReactiveUserDetailsService();
    }


    /**
     * 缓存用户权限信息，测试时可以使用
    */
    //@Bean
    //ReactiveUserDetailsService reactiveUserDetailsService(){
    //    User.UserBuilder builder = User.builder().passwordEncoder(passwordEncoder()::encode);
    //    UserDetails userDetails1 = builder.username("admin").password("123456").roles("admin").build();
    //    UserDetails userDetails2 = builder.username("user").password("123456").roles("user").build();
    //
    //    MapReactiveUserDetailsService mapReactiveUserDetailsService = new MapReactiveUserDetailsService(userDetails1,userDetails2);
    //    return mapReactiveUserDetailsService;
    //}

    /**
     * 注册PasswordEncoder
    */
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}


