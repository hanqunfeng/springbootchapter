package com.example.config;

/**
 * <h1>SecurityConfig</h1>
 * Created by hanqf on 2023/8/23 16:16.
 */


import com.example.security.CustomAccessDeniedHandler;
import com.example.security.CustomAuthExceptionEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Autowired
    private ObjectMapper objectMapper;

    //从配置文件中获取OAuth2 Jwt令牌签发者的uri
//    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    String issuerUri;

    /**
     * access_token无效或过期时的处理方式
    */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new CustomAuthExceptionEntryPoint(objectMapper);
    }


    /**
     * access_token认证后没有对应的权限时的处理方式
    */
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler(objectMapper);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/demo").permitAll()
                // 默认认证用户的权限是 scope 中的值，前缀是 SCOPE_，比如这里的 SCOPE_user:info
                .requestMatchers("/resource").hasAuthority("SCOPE_user:info")
                // 如果希望使用用户角色进行验证，则需要auth-server端进行 OAuth2TokenCustomizer 配置，另外resource也要加上 jwtAuthenticationConverter 的配置
                .requestMatchers("/jwt").hasAuthority("ROLE_admin")
                .anyRequest().authenticated());

        // 开启OAuth2资源认证
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwtConfigurer -> jwtConfigurer
//                        .decoder(jwtDecoder()) //设置JWT解密器，这里不需要配置，会自动通过 issuer-uri 的配置到服务端查找
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
                            return new JwtAuthenticationToken(jwt, authorities);
                        })
                )
                        .bearerTokenResolver(bearerTokenResolver())
        );

        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler()));
        return http.build();
    }

    private BearerTokenResolver bearerTokenResolver(){
        DefaultBearerTokenResolver resolver = new DefaultBearerTokenResolver();
        // 设置是否允许将 token 放到查询参数 access_token 中
        resolver.setAllowUriQueryParameter(true); // 默认 false
        // 设置 请求 header 中，负责承载 token 的属性名称
        resolver.setBearerTokenHeaderName("Authorization"); // 默认 Authorization
        return resolver;
    }

    // 不需要这样做，默认会提供一个 JwtDecoder
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        return JwtDecoders.fromIssuerLocation(issuerUri);
//    }


}

