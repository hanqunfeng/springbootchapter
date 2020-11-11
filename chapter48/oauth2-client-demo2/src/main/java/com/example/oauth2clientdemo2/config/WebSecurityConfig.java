package com.example.oauth2clientdemo2.config;

import com.example.oauth2clientdemo2.security.CustomAccessDeniedHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2020/11/9 18:11.
 */
@Configuration
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${oauth2.server.logout}")
    private String oauth2_server_logout;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //logout跳转到认证服务器的logout
        http.logout().logoutSuccessUrl(oauth2_server_logout);

        //配置认证规则
        http.authorizeRequests()
                .anyRequest().authenticated();

        //开启oauth2登录认证
        http.oauth2Login()
                .and().oauth2Client();

        http.csrf().disable();

        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);
    }

    /**
     * 调用Resource Server服务使用RestTemplate，当调用的Resource Server时候我们时需要使用Bearer Token在头部传递Access Token；
     * RestTemplateAutoConfiguration已经给我们自动配置了RestTemplateBuilder来配置RestTemplate，我们需要通过RestTemplateCustomizer来对RestTemplate来定制
     */
    @Bean
    RestTemplateCustomizer restTemplateCustomizer(OAuth2AuthorizedClientService clientService) {
        return restTemplate -> { //1 RestTemplateCustomizer时函数接口，入参是RestTemplate
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            if (CollectionUtils.isEmpty(interceptors)) {
                interceptors = new ArrayList<>();
            }
            interceptors.add((request, body, execution) -> { //2 通过增加RestTemplate拦截器，让每次请求添加Bearer Token（Access Token）；ClientHttpRequestInterceptor是函数接口，可用Lambda表达式来实现
                OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
                String clientRegistrationId = auth.getAuthorizedClientRegistrationId();
                String principalName = auth.getName();
                OAuth2AuthorizedClient client =
                        clientService.loadAuthorizedClient(clientRegistrationId, principalName); //3 OAuth2AuthorizedClientService可获得用户的OAuth2AuthorizedClient
                String accessToken = client.getAccessToken().getTokenValue(); //4 OAuth2AuthorizedClient可获得用户Access Token
                request.getHeaders().add("Authorization", "Bearer " + accessToken); //5 将Access Token通过头部的Bearer Token中访问Resource Server

                log.info(String.format("请求地址: %s", request.getURI()));
                log.info(String.format("请求头信息: %s", request.getHeaders()));

                ClientHttpResponse response = execution.execute(request, body);
                log.info(String.format("响应头信息: %s", response.getHeaders()));

                return response;
            });
            restTemplate.setInterceptors(interceptors);
        };
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

}


