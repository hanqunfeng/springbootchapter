package com.example.controller;

import com.example.exchange.ResourceServerClient;
import com.example.response.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2023/8/22 17:20.
 */

@RestController
public class IndexController {

    @Autowired
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @Autowired
    private ResourceServerClient resourceServerClient;

    @RequestMapping("/")
    public String index() {
        return "hello world";
    }

    @RequestMapping(value = "/user")
    public Principal user(Principal principal) {
        //principal在经过security拦截后，是org.springframework.security.authentication.UsernamePasswordAuthenticationToken
        //在经OAuth2拦截后，是OAuth2Authentication
        return principal;
    }

    /**
     * 效果同/user
     */
    @RequestMapping(value = "/user2")
    public Authentication user2(Authentication authentication) {
        return authentication;
    }

    /**
     * 只打印用户属性信息，由于user-info-uri指定的接口中返回了自定义扩展属性，所以这里可以获取到扩展属性
     */
    @RequestMapping(value = "/user3")
    public Map<String, Object> user3(Principal principal) {
        Map<String, Object> attributes = null;
        //返回扩展属性
        if (principal instanceof OAuth2AuthenticationToken auth2AuthenticationToken) {
            attributes = auth2AuthenticationToken.getPrincipal().getAttributes();
            attributes.forEach((k, v) -> System.out.println(k + "=" + v));
        }
        return attributes;
    }

    /**
     * 只打印用户权限信息
     */
    @RequestMapping(value = "/user4")
    public Collection<? extends GrantedAuthority> user4(Principal principal) {
        Collection<? extends GrantedAuthority> authorities = null;
        //返回权限信息
        if (principal instanceof OAuth2AuthenticationToken auth2AuthenticationToken) {
            authorities = auth2AuthenticationToken.getPrincipal().getAuthorities();
            authorities.forEach(s -> System.out.println(s.getAuthority()));
        }

        return authorities;
    }


    /**
     * 打印token
     */
    @GetMapping("/token")
    public String token(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        return authorizedClient.getAccessToken().getTokenValue();
    }


    @GetMapping("/token2")
    public String token(Authentication authentication) {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService
                .loadAuthorizedClient(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(), oAuth2AuthenticationToken.getName());
        String jwtAccessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
        String jwtRefrechToken = oAuth2AuthorizedClient.getRefreshToken().getTokenValue();
        return "<b>JWT Access Token: </b>" + jwtAccessToken + "<br/><br/><b>JWT Refresh Token:  </b>" + jwtRefrechToken;
    }

    /**
     * 请求资源服务器的内容
     */
    @RequestMapping("/resource")
    public AjaxResponse resource() {
        return resourceServerClient.resource();
    }

    @RequestMapping("/jwt")
    public AjaxResponse jwt() {
        System.out.println("jwt");
        return resourceServerClient.jwt();
    }

}
