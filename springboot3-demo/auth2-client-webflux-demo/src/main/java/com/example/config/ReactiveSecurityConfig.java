package com.example.config;

/**
 * <h1>ReactiveSecurityConfig</h1>
 * Created by hanqf on 2023/8/28 16:14.
 */


import com.example.security.CustomReactiveAuthorizationManager;
import com.example.security.CustomServerAccessDeniedHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.R2dbcReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

/**
 * <h1>安全认证配置</h1>
 * Created by hanqf on 2020/11/19 10:26.
 */


@Slf4j
@Configuration
//@EnableWebFluxSecurity //非必要
@EnableReactiveMethodSecurity //启用@PreAuthorize注解配置
public class ReactiveSecurityConfig {

    /**
     * rbac权限认证
    */

    @Autowired
    private CustomReactiveAuthorizationManager customReactiveAuthorizationManager;


    /**
     * access_token认证后没有对应的权限时的处理方式
     */
    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return new CustomServerAccessDeniedHandler();
    }

    /**
     * 注册安全验证规则
     * 配置方式与HttpSecurity基本一致
     */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) { //定义SecurityWebFilterChain对安全进行控制，使用ServerHttpSecurity构造过滤器链；

        http.authorizeExchange(auth -> auth
                .pathMatchers("/demo").permitAll()
                // 默认认证用户的权限是 scope 中的值，前缀是 SCOPE_，比如这里的 SCOPE_user:info
                .pathMatchers("/user2").hasAuthority("SCOPE_user:info")
                // 不支持使用用户角色进行验证
//                .pathMatchers("/user3").hasAuthority("ROLE_admin")
                //基于权限管理模型认证
                .pathMatchers("/rbac").access(customReactiveAuthorizationManager)
                //其它路径登录后访问
                .anyExchange().authenticated());

        //oauth2 login，不加这个不会开启oauth2登录页面
        http.oauth2Login(Customizer.withDefaults());

        // oauth2 client配置
        http.oauth2Client(Customizer.withDefaults());

//        http.oauth2Client(client -> {
//
//        });

        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                .accessDeniedHandler(accessDeniedHandler()));


        return http.build();

    }

    /**
     * 用户登录 token 信息保存到到数据库：R2dbcReactiveOAuth2AuthorizedClientService
     *
     * 默认保存到内存：InMemoryReactiveOAuth2AuthorizedClientService
    */
    @Bean
    public ReactiveOAuth2AuthorizedClientService r2dbcReactiveOAuth2AuthorizedClientService(DatabaseClient databaseClient, ReactiveClientRegistrationRepository clientRegistrationRepository) {
        log.info("R2dbcReactiveOAuth2AuthorizedClientService");
        return new R2dbcReactiveOAuth2AuthorizedClientService(databaseClient, clientRegistrationRepository);
    }

}
