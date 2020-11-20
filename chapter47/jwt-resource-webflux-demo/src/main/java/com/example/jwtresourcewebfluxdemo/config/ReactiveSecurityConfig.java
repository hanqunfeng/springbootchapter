package com.example.jwtresourcewebfluxdemo.config;

import com.example.jwtresourcewebfluxdemo.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

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
     * 将登陆后的用户及权限信息存入session中，非必须
     * @return
     */
    @Bean
    ServerSecurityContextRepository serverSecurityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }


    /**
     * 注册UserDetailsService
    */
    @Bean
    CustomReactiveUserDetailsService reactiveUserDetailsService(){
        return new CustomReactiveUserDetailsService();
    }

    /**
     * 注册PasswordEncoder
    */
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}


