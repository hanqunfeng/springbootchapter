package com.example.oauth2clientdemo.security;

import com.example.oauth2clientdemo.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>获取资源服务器数据工具类</h1>
 * Created by hanqf on 2020/11/7 23:17.
 */

@Slf4j
public class OAuth2ResourceRestTemplateUtil {

    private static RestTemplate restTemplate = null;
    //工厂类
    private static SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    //拦截器
    private static List<ClientHttpRequestInterceptor> list = new ArrayList<>();
    static {
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        list.add(new OAuth2ResourceRestTemplateUtil.RetryIntercepter()); //重试拦截器
        list.add(new OAuth2ResourceRestTemplateUtil.HeadersLoggingInterceper()); //header拦截器
    }

    private OAuth2ResourceRestTemplateUtil() {
    }

    /**
     * 饱汉模式(懒汉模式)--双重加锁检查DCL（Double Check Lock）
    */
    public static RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            synchronized(OAuth2ResourceRestTemplateUtil.class) {
                if (restTemplate == null) {
                    restTemplate = new RestTemplate(factory);
                    restTemplate.setInterceptors(list);
                }
            }
        }
        return restTemplate;
    }

    /**
     * <p>重试拦截器</p>
     */
    private static class RetryIntercepter implements ClientHttpRequestInterceptor {

        private int maxRetry = 3;//最大重试次数，默认3次
        private int retryNum = 0;

        public RetryIntercepter() {

        }

        public RetryIntercepter(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
            ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
            if (!response.getStatusCode().equals(HttpStatus.OK) && retryNum < maxRetry) {
                retryNum++;
                response = clientHttpRequestExecution.execute(httpRequest, bytes);

            }
            return response;
        }
    }

    /**
     * <p>Headers拦截器</p>
     */
    private static class HeadersLoggingInterceper implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException, CustomException {

            try {
                OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
                String tokenType = details.getTokenType();
                String tokenValue = details.getTokenValue();
                httpRequest.getHeaders().add("authorization", tokenType + " " + tokenValue);
            } catch (Exception e) {
                throw new CustomException(HttpStatus.FORBIDDEN, e.getMessage(), e.getClass().getName());
            }
            log.info(String.format("请求地址: %s", httpRequest.getURI()));
            log.info(String.format("请求头信息: %s", httpRequest.getHeaders()));
            ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
            log.info(String.format("响应头信息: %s", response.getHeaders()));
            return response;
        }
    }


}
