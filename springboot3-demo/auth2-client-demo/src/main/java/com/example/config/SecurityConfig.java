package com.example.config;

/**
 * <h1>SecurityConfig</h1>
 * Created by hanqf on 2023/8/22 16:42.
 */


import com.example.security.CustomAccessDeniedHandler;
import com.example.security.RbacService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RbacService rbacService;

    @Autowired
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;


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
                // 认证用户的权限是 scope 中的值，前缀是 SCOPE_，比如这里的 SCOPE_user:info
                .requestMatchers("/user2").hasAuthority("SCOPE_user:info")
                // 不支持使用用户角色进行验证
                // .requestMatchers("/user3").hasAuthority("ROLE_admin")

                // 可以使用rbac进行权限认证，自定义认证规则
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.do*"), AntPathRequestMatcher.antMatcher("/**/*.do*")).access((authentication, context) ->
                        new AuthorizationDecision(rbacService.hasPerssion(context.getRequest(),authentication.get())))

                // 其它未匹配请求都需要登录后才能访问
                .anyRequest().authenticated());


        //oauth2 login，不加这个不会开启oauth2登录页面
        http.oauth2Login(Customizer.withDefaults());

        // oauth2 client配置
//        http.oauth2Client(Customizer.withDefaults());

        http.oauth2Client(client -> client
                .authorizedClientService(oAuth2AuthorizedClientService));

        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                .accessDeniedHandler(accessDeniedHandler()));

        return http.build();
    }


}

