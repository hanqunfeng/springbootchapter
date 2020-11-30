package com.example.oauth2clientwebfluxdemo.config;


import com.example.oauth2clientwebfluxdemo.security.CustomReactiveAuthorizationManager;
import com.example.oauth2clientwebfluxdemo.security.CustomServerAccessDeniedHandler;
import com.example.oauth2clientwebfluxdemo.security.CustomServerAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * <h1>安全认证配置</h1>
 * Created by hanqf on 2020/11/19 10:26.
 */


@Configuration
@EnableWebFluxSecurity //非必要
@EnableReactiveMethodSecurity //启用@PreAuthorize注解配置
public class ReactiveSecurityConfig {

    @Value("${oauth2.server.logout}")
    private String oauth2_server_logout;

    @Autowired
    private CustomServerAccessDeniedHandler customServerAccessDeniedHandler;

    @Autowired
    private CustomServerAuthenticationEntryPoint customServerAuthenticationEntryPoint;

    @Autowired
    private CustomReactiveAuthorizationManager customReactiveAuthorizationManager;

    //@Autowired
    //private CustomReactiveClientRegistrationRepository customReactiveClientRegistrationRepository;

    /**
     * 注册安全验证规则
     * 配置方式与HttpSecurity基本一致
     */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) { //定义SecurityWebFilterChain对安全进行控制，使用ServerHttpSecurity构造过滤器链；
        return http.authorizeExchange()
                //.anyExchange().authenticated() //所有请求都需要通过认证
                .pathMatchers("/").authenticated()
                //需要具备相应的角色才能访问
                .pathMatchers("/user/**", "/user2/**").hasAuthority("SCOPE_any")
                //不需要登录就可以访问
                .pathMatchers("/login","/webjars/**").permitAll()

                //其它路径需要根据指定的方法判断是否有权限访问，基于权限管理模型认证
                .anyExchange().access(customReactiveAuthorizationManager)
                //.anyExchange().permitAll()
                .and()
                .csrf().disable() //关闭CSRF（Cross-site request forgery）跨站请求伪造

                //开启oauth2登录认证
                .oauth2Login()
                .and()
                //开启基于oauth2的客户端信息
                .oauth2Client()
                //客户端信息基于数据库，基于内存去掉下面配置即可
                //.clientRegistrationRepository(customReactiveClientRegistrationRepository)
                .and().exceptionHandling()
                .accessDeniedHandler(customServerAccessDeniedHandler)
                .authenticationEntryPoint(customServerAuthenticationEntryPoint)
                .and()
                .build();
    }




}


