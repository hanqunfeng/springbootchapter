package com.example.oauth2resourceserverdemo2.config;

import com.example.oauth2resourceserverdemo2.security.CustomAccessDeniedHandler;
import com.example.oauth2resourceserverdemo2.security.CustomAuthExceptionEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <h1>WebSecurityConfig</h1>
 * Created by hanqf on 2020/11/11 17:01.
 */

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private CustomAuthExceptionEntryPoint customAuthExceptionEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //开启跨域
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //权限控制
        http.authorizeRequests()//登录成功就可以访问
                .antMatchers("/res/**", "/userInfo/**").authenticated()
                //需要具备相应的角色才能访问
                .antMatchers("/user/**").hasAnyRole("admin", "user")
                //不需要登录就可以访问
                .antMatchers("/swagger-ui/**", "/v3/api-docs**").permitAll()
                //其它路径需要根据指定的方法判断是否有权限访问，基于权限管理模型认证
                .anyRequest().access("@rbacService.hasPerssion(request,authentication)");


        //鉴权时只支持Bearer Token的形式，不支持url后加参数access_token
        http.oauth2ResourceServer()//开启oauth2资源认证
                .jwt() //token为jwt
                //默认情况下，权限是scope，而我们希望使用的是用户的角色，所以这里需要通过转换器进行处理
                .jwtAuthenticationConverter(jwt -> { //通过自定义Converter来指定权限，Converter是函数接口，当前上下问参数为JWT对象
                    Collection<SimpleGrantedAuthority> authorities =
                            ((Collection<String>) jwt.getClaims()
                                    .get("authorities")).stream() //获取JWT中的authorities
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toSet());

                    //如果希望保留scope的权限，可以取出scope数据然后合并到一起，这样因为不是以ROLE_开头，所以需要使用hasAuthority('SCOPE_any')的形式
                    Collection<SimpleGrantedAuthority> scopes = ((Collection<String>) jwt.getClaims()
                            .get("scope")).stream().map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                            .collect(Collectors.toSet());
                    //合并权限
                    authorities.addAll(scopes);
                    return new JwtAuthenticationToken(jwt, authorities);
                });

        http.exceptionHandling()
                //access_token无效或过期时的处理方式
                .authenticationEntryPoint(customAuthExceptionEntryPoint)
                //access_token认证后没有对应的权限时的处理方式
                .accessDeniedHandler(customAccessDeniedHandler);
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
