package com.example.config;

import com.example.mysql.SysUserRepository;
import com.example.security.CustomReactiveUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

/**
 * <h1>ReactiveSecurityConfig</h1>
 * Created by hanqf on 2023/8/30 17:11.
 */

@Configuration
@EnableReactiveMethodSecurity //启用@PreAuthorize注解配置
public class ReactiveSecurityConfig {

    @Autowired
    private SysUserRepository sysUserRepository;

    /**
     * 注册安全验证规则
     * 配置方式与HttpSecurity基本一致
     */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) { //定义SecurityWebFilterChain对安全进行控制，使用ServerHttpSecurity构造过滤器链；
        http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                .pathMatchers("/redis/**","/user/**").permitAll()
                .anyExchange().authenticated() // 其余请求都需要通过认证
        );

        //关闭CSRF（Cross-site request forgery）跨站请求伪造
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        //支持HTTP Basic方式登录
        http.httpBasic(Customizer.withDefaults());

        //支持login页面登录
        http.formLogin(Customizer.withDefaults());

        return http.build();
    }

    /**
     * 将登陆后的用户及权限信息存入session中，非必须
     *
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
    ReactiveUserDetailsService reactiveUserDetailsService() {
        // 基于内存
        User.UserBuilder builder = User.builder().passwordEncoder(passwordEncoder()::encode);
        UserDetails userDetails1 = builder.username("admin").password("123456").roles("admin").build();
        UserDetails userDetails2 = builder.username("guest").password("123456").roles("guest").build();
        UserDetails userDetails3 = builder.username("user").password("123456").roles("user").build();
//        return new MapReactiveUserDetailsService(userDetails1, userDetails2, userDetails3);

        // 基于数据库
        return new CustomReactiveUserDetailsService(sysUserRepository);
    }

    /**
     * 注册PasswordEncoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
