package com.hanqf.config;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * <h1></h1>
 * Created by hanqf on 2024/5/17 16:28.
 */

@Configuration
public class Config {

    /**
     * spring ai使用RestClient的实例进行网络请求，默认为OkHttp3Client
     * 这里设置连接超时时间
    */
    @Bean
    @Scope("prototype")
    RestClient.Builder restClientBuilder(RestClientBuilderConfigurer restClientBuilderConfigurer) {
        RestClient.Builder builder = RestClient.builder().requestFactory(
                ClientHttpRequestFactories.get(
                        //设置连接超时时间，默认只有10s，调用接口有时会超时
                        new ClientHttpRequestFactorySettings(Duration.ofSeconds(30), Duration.ofSeconds(30), (SslBundle)null)
                ));
        return restClientBuilderConfigurer.configure(builder);
    }

}
