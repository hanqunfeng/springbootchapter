package com.example.oauth2resourceserverdemo.config;

import com.example.oauth2resourceserverdemo.security.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * OAuth2的资源服务配置类(主要作用是配置资源受保护的OAuth2策略)
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private TokenStore jwtTokenStore;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(jwtTokenStore);
    }

    /**
     * 配置受OAuth2保护的URL资源。
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                //登录成功就可以访问
                .antMatchers("/res/**").authenticated()
                //需要具备相应的角色才能访问
                .antMatchers("/user/**").access("hasRole('admin') or hasRole('user')")
                //不需要登录就可以访问
                .antMatchers("/swagger-ui/**","/v3/api-docs**").permitAll()
                //其它路径只要登录就可以访问
                .anyRequest().authenticated();
        //没有访问权限时的处理方式
        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);
    }
}
