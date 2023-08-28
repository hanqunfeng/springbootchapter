package com.example.config;

/**
 * <h1>ReactiveSecurityConfig</h1>
 * Created by hanqf on 2023/8/28 16:14.
 */


import com.example.security.CustomReactiveAuthorizationManager;
import com.example.security.CustomServerAccessDeniedHandler;
import com.example.security.CustomServerAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <h1>安全认证配置</h1>
 * Created by hanqf on 2020/11/19 10:26.
 */


@Configuration
//@EnableWebFluxSecurity //非必要
@EnableReactiveMethodSecurity //启用@PreAuthorize注解配置
public class ReactiveSecurityConfig {


    @Autowired
    private CustomReactiveAuthorizationManager customReactiveAuthorizationManager;

    /**
     * access_token无效或过期时的处理方式
     */
    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomServerAuthenticationEntryPoint();
    }


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
                .pathMatchers("/resource").hasAuthority("SCOPE_user:info")
                // 如果希望使用用户角色进行验证，则需要auth-server端进行 OAuth2TokenCustomizer 配置，另外resource也要加上 jwtAuthenticationConverter 的配置
                .pathMatchers("/jwt").hasAuthority("ROLE_admin")
                //基于权限管理模型认证
                .pathMatchers("/rbac").access(customReactiveAuthorizationManager)
                //其它路径登录后访问
                .anyExchange().authenticated());

        // 开启OAuth2资源认证
        http.oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                        //默认情况下，权限是scope，而我们希望使用的是用户的角色，所以这里需要通过转换器进行处理
                                        .jwtAuthenticationConverter(jwt -> { //通过自定义Converter来指定权限，Converter是函数接口，当前上下问参数为JWT对象
                                            // 这里的 authorities 是 通过 tokenCustomizer() 方法放到claim中的
                                            Collection<SimpleGrantedAuthority> authorities = ((Collection<String>) jwt.getClaims().get("authorities"))
                                                    .stream() //获取JWT中的authorities
                                                    .map(SimpleGrantedAuthority::new)
                                                    .collect(Collectors.toSet());

                                            //如果希望保留scope的权限，可以取出scope数据然后合并到一起，这样因为不是以ROLE_开头，所以需要使用hasAuthority('SCOPE_any')的形式
                                            Collection<SimpleGrantedAuthority> scopes = ((Collection<String>) jwt.getClaims().get("scope"))
                                                    .stream()
                                                    .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                                                    .collect(Collectors.toSet());
                                            //合并权限
                                            authorities.addAll(scopes);
                                            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
                                        })
                        )
        );

        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler()));


        return http.build();

    }

}
