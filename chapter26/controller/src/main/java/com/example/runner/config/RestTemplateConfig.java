package com.example.runner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * <p></p>
 * Created by hanqf on 2020/4/22 16:52.
 */

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    //异步请求过时了，推荐使用spring-boot-starter-webflux中的WebClient
    //public AsyncRestTemplate

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        //默认的是JDK提供http连接，需要的话可以替换为例如Apache HttpComponents、Netty或OkHttp等其它HTTP library。
        //例如：OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(OkHttpClient);
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return factory;
    }

}
