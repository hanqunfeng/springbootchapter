package com.example.oauth2resourceserverdemo.config;

import com.example.oauth2resourceserverdemo.security.CustomAccessDeniedHandler;
import com.example.oauth2resourceserverdemo.security.CustomAuthExceptionEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * OAuth2的资源服务配置类(主要作用是配置资源受保护的OAuth2策略)
 */
@Configuration
@EnableResourceServer
@Import(OAuth2MethodSecurityConfiguration.class)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private TokenStore jwtTokenStore;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private CustomAuthExceptionEntryPoint customAuthExceptionEntryPoint;
    @Autowired
    private OAuth2WebSecurityExpressionHandler expressionHandler;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(jwtTokenStore)
                //token无效或过期时的处理方式
                .authenticationEntryPoint(customAuthExceptionEntryPoint)
                //没有访问权限时的处理方式
                .accessDeniedHandler(customAccessDeniedHandler)
                //设置表达式处理器，解决不能解析"@rbasService.hasPerssion(request,authentication)"表达式的问题
                .expressionHandler(expressionHandler);
    }

    @Bean
    public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(ApplicationContext applicationContext) {
        OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;
    }

    /**
     * 配置受OAuth2保护的URL资源。
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //开启跨域
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                //登录成功就可以访问
                .antMatchers("/res/**").authenticated()
                //需要具备相应的角色才能访问
                .antMatchers("/user/**").access("hasRole('admin') or hasRole('user')")
                //不需要登录就可以访问
                .antMatchers("/swagger-ui/**", "/v3/api-docs**").permitAll()
                //其它路径需要根据指定的方法判断是否有权限访问，基于权限管理模型认证
                .anyRequest().access("@rbacService.hasPerssion(request,authentication)");

    }


    /**
     * 认证事件监听器，打印日志
     * <p>
     * 如：认证失败/成功、注销，等等
     */
    @Bean
    public org.springframework.security.authentication.event.LoggerListener loggerListener() {
        org.springframework.security.authentication.event.LoggerListener loggerListener = new org.springframework.security.authentication.event.LoggerListener();
        return loggerListener;
    }

    /**
     * 资源访问事件监听器，打印日志
     * <p>
     * 如：没有访问权限
     */
    @Bean
    public org.springframework.security.access.event.LoggerListener eventLoggerListener() {
        org.springframework.security.access.event.LoggerListener eventLoggerListener = new org.springframework.security.access.event.LoggerListener();
        return eventLoggerListener;
    }


    /**
     * 跨域配置类
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        //开放哪些ip、端口、域名的访问权限，星号表示开放所有域
        corsConfiguration.addAllowedOrigin("*");
        //corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:8080","http://localhost:8081"));
        //开放哪些Http方法，允许跨域访问
        corsConfiguration.addAllowedMethod("*");
        //corsConfiguration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
        //允许HTTP请求中的携带哪些Header信息
        corsConfiguration.addAllowedHeader("*");
        //是否允许发送Cookie信息
        corsConfiguration.setAllowCredentials(true);

        //添加映射路径，“/**”表示对所有的路径实行全局跨域访问权限的设置
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", corsConfiguration);

        return configSource;
    }
}
