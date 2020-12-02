package com.example.oauth2clientwebfluxdemo.controller;

import com.example.oauth2clientwebfluxdemo.exception.AjaxResponse;
import com.example.oauth2clientwebfluxdemo.exception.CustomException;
import com.example.oauth2clientwebfluxdemo.exception.CustomExceptionType;
import com.example.oauth2clientwebfluxdemo.security.CustomServerOAuth2AuthorizedClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>获取资源服务器数据</h1>
 * Created by hanqf on 2020/11/7 22:47.
 */

@RestController
@RequestMapping("/res")
public class ResController {

    private static final WebClient CLIENT = WebClient.create("http://localhost:8083");


    @Autowired
    private CustomServerOAuth2AuthorizedClientRepository customServerOAuth2AuthorizedClientRepository;

    /**
     * 获取资源服务器的数据
     */
    @RequestMapping("/res1")
    public Mono<AjaxResponse> getRes(Authentication principal, ServerWebExchange exchange) {
        if (principal instanceof OAuth2AuthenticationToken) {
            String authorizedClientRegistrationId = ((OAuth2AuthenticationToken) principal).getAuthorizedClientRegistrationId();
            Mono<OAuth2AuthorizedClient> oAuth2AuthorizedClientMono = customServerOAuth2AuthorizedClientRepository.loadAuthorizedClient(authorizedClientRegistrationId, principal, exchange);

            return oAuth2AuthorizedClientMono.flatMap(client -> {
                String tokenValue = client.getAccessToken().getTokenValue();
                String tokenType = client.getAccessToken().getTokenType().getValue();
                return CLIENT.get()
                        .uri("/res/res1")
                        //增加了basic安全认证，所以这里需要传递header认证信息
                        .header(HttpHeaders.AUTHORIZATION,
                                tokenType + " " + tokenValue)
                        .retrieve()//异步接收服务端响应
                        .bodyToMono(AjaxResponse.class)
                        .retry(3)
                        .defaultIfEmpty(AjaxResponse.success("返回结果为Null"));
            });
        } else {
            return Mono.just(AjaxResponse.error(new CustomException(CustomExceptionType.SYSTEM_ERROR,"Authentication类型转换异常")));
        }
    }

    @RequestMapping("/user")
    public Mono<AjaxResponse> getUser(Authentication principal, ServerWebExchange exchange) {
        if (principal instanceof OAuth2AuthenticationToken) {
            String authorizedClientRegistrationId = ((OAuth2AuthenticationToken) principal).getAuthorizedClientRegistrationId();
            Mono<OAuth2AuthorizedClient> oAuth2AuthorizedClientMono = customServerOAuth2AuthorizedClientRepository.loadAuthorizedClient(authorizedClientRegistrationId, principal, exchange);

            return oAuth2AuthorizedClientMono.flatMap(client -> {
                String tokenValue = client.getAccessToken().getTokenValue();
                String tokenType = client.getAccessToken().getTokenType().getValue();
                return CLIENT.post()
                        .uri("/user")
                        //增加了basic安全认证，所以这里需要传递header认证信息
                        .header(HttpHeaders.AUTHORIZATION,
                                tokenType + " " + tokenValue)
                        .retrieve()//异步接收服务端响应
                        .bodyToMono(AjaxResponse.class)
                        .retry(3)
                        .defaultIfEmpty(AjaxResponse.success("返回结果为Null"));
            });
        } else {
            return Mono.just(AjaxResponse.error(new CustomException(CustomExceptionType.SYSTEM_ERROR,"Authentication类型转换异常")));
        }
    }

    @RequestMapping("/rbac")
    public Mono<AjaxResponse> getRbac(Authentication principal, ServerWebExchange exchange) {
        if (principal instanceof OAuth2AuthenticationToken) {
            String authorizedClientRegistrationId = ((OAuth2AuthenticationToken) principal).getAuthorizedClientRegistrationId();
            Mono<OAuth2AuthorizedClient> oAuth2AuthorizedClientMono = customServerOAuth2AuthorizedClientRepository.loadAuthorizedClient(authorizedClientRegistrationId, principal, exchange);

            return oAuth2AuthorizedClientMono.flatMap(client -> {
                String tokenValue = client.getAccessToken().getTokenValue();
                String tokenType = client.getAccessToken().getTokenType().getValue();
                return CLIENT.get()
                        .uri("/rbac")
                        //增加了basic安全认证，所以这里需要传递header认证信息
                        .header(HttpHeaders.AUTHORIZATION,
                                tokenType + " " + tokenValue)
                        .retrieve()//异步接收服务端响应
                        .bodyToMono(AjaxResponse.class)
                        .retry(3)
                        .defaultIfEmpty(AjaxResponse.success("返回结果为Null"));
            });
        } else {
            return Mono.just(AjaxResponse.error(new CustomException(CustomExceptionType.SYSTEM_ERROR,"Authentication类型转换异常")));
        }
    }

    @RequestMapping("/userInfo")
    public Mono<Map> getuserInfo(Authentication principal, ServerWebExchange exchange) {
        if (principal instanceof OAuth2AuthenticationToken) {
            String authorizedClientRegistrationId = ((OAuth2AuthenticationToken) principal).getAuthorizedClientRegistrationId();
            Mono<OAuth2AuthorizedClient> oAuth2AuthorizedClientMono = customServerOAuth2AuthorizedClientRepository.loadAuthorizedClient(authorizedClientRegistrationId, principal, exchange);

            return oAuth2AuthorizedClientMono.flatMap(client -> {
                String tokenValue = client.getAccessToken().getTokenValue();
                String tokenType = client.getAccessToken().getTokenType().getValue();
                return CLIENT.get()
                        .uri("/userInfo")
                        //增加了basic安全认证，所以这里需要传递header认证信息
                        .header(HttpHeaders.AUTHORIZATION,
                                tokenType + " " + tokenValue)
                        .retrieve()//异步接收服务端响应
                        .bodyToMono(Map.class)
                        .retry(3)
                        .defaultIfEmpty(new HashMap());
            });
        } else {
            return Mono.error(new CustomException(CustomExceptionType.SYSTEM_ERROR,"Authentication类型转换异常"));
        }
    }

}
