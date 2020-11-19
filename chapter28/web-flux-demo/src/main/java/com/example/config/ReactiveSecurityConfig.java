package com.example.config;

import com.example.mysql.service.CustomReactiveUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * <h1>安全认证配置</h1>
 * Created by hanqf on 2020/11/19 10:26.
 */


@Configuration
@EnableReactiveMethodSecurity//开启方法安全的支持
public class ReactiveSecurityConfig {

    /**
     * 注册安全验证规则
    */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http){ //定义SecurityWebFilterChain对安全进行控制，使用ServerHttpSecurity构造过滤器链；
        return http.authorizeExchange()
                .anyExchange().authenticated() //所有请求都需要通过认证
                .and()
                .csrf().disable() //关闭CSRF（Cross-site request forgery）跨站请求伪造
                .httpBasic() //支持HTTP Basic方式登录
                .and().formLogin()//支持login页面登录
                .and()
                .build();
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


