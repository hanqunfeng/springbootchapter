package com.example.config;

import com.example.security.JwtProperties;
import com.example.security.JwtToken;
import com.example.security.support.CustomAccessDeniedHandler;
import com.example.security.support.CustomSecurityProperties;
import com.example.security.support.JwtAuthenticationTokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.event.LoggerListener;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * <h1>WebSecurityConfig</h1>
 * Created by hanqf on 2023/8/21 12:20.
 */

@Slf4j
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtToken jwtToken;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private CustomSecurityProperties securityProperties;

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler(objectMapper);
    }


    @Bean
    // 密码加密策略
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        User.UserBuilder builder = User.builder().passwordEncoder(passwordEncoder()::encode);
        UserDetails userDetails1 = builder.username("admin").password("123456").roles("admin").build();
        UserDetails userDetails2 = builder.username("guest").password("123456").roles("guest").build();
        UserDetails userDetails3 = builder.username("user").password("123456").roles("user").build();
        return new InMemoryUserDetailsManager(userDetails1, userDetails2, userDetails3);
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter(userDetailsService(), jwtToken, jwtProperties, objectMapper, securityProperties);
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

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        log.info("AuthenticationManager");
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("HttpSecurity");
        //解决不允许显示在iframe的问题
        http.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // 设置拦截规则
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                .requestMatchers(Arrays.stream(securityProperties.getPermitAll()).map(AntPathRequestMatcher::antMatcher).toList().toArray(AntPathRequestMatcher[]::new)).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*")).authenticated()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/**/*")).authenticated());

        http.exceptionHandling(exceptionHandlingCustomizer -> exceptionHandlingCustomizer
                .accessDeniedHandler(customAccessDeniedHandler()));


        //关闭csrf，默认开启，csrf不会拦截get请求
        http.csrf(AbstractHttpConfigurer::disable);


        // session管理
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                //无状态策略
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        //设置authenticationProvider，authenticationProvider中设置的密码策略，这样userDetailsService中的密码就不需要加密了
        http.authenticationProvider(authenticationProvider());

        // 设置过滤器，这里是验证码过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 认证日志
     */
    @Bean
    public LoggerListener loggerListener() {
        log.info("org.springframework.security.authentication.event.LoggerListener");
        return new LoggerListener();
    }


}
