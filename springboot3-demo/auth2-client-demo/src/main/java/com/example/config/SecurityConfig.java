package com.example.config;

/**
 * <h1>SecurityConfig</h1>
 * Created by hanqf on 2023/8/22 16:42.
 */


import com.example.security.CustomAccessDeniedHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * access_token认证后没有对应的权限时的处理方式
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler(objectMapper);
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //关闭csrf
        http.csrf(AbstractHttpConfigurer::disable);
        //所有请求都需经过授权认证
        http.authorizeHttpRequests(authorize -> authorize
                // 默认认证用户的权限是 scope 中的值，前缀是 SCOPE_，比如这里的 SCOPE_user:info
                .requestMatchers("/user2").hasAuthority("SCOPE_user:info")
                // 不支持使用用户角色进行验证
                // .requestMatchers("/user3").hasAuthority("ROLE_admin")
                .anyRequest().authenticated());


        //oauth2 login，不加这个不会开启oauth2登录页面
        http.oauth2Login(Customizer.withDefaults());

        http.oauth2Client(Customizer.withDefaults());

        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                .accessDeniedHandler(accessDeniedHandler()));

        return http.build();
    }
}

