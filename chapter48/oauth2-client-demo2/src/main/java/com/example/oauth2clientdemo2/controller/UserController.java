package com.example.oauth2clientdemo2.controller;


import com.example.oauth2clientdemo2.exception.AjaxResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

/**
 * <h1>res</h1>
 * Created by hanqf on 2020/11/6 17:22.
 */

@RestController
public class UserController {

    /**
     * 用户信息
     */
    @PreAuthorize("#oauth2.hasScope('any')")
    @RequestMapping(value = "/user")
    public AjaxResponse user(Principal principal) {
        //principal在经过security拦截后，是org.springframework.security.authentication.UsernamePasswordAuthenticationToken
        //在经OAuth2拦截后，是OAuth2Authentication
        return AjaxResponse.success(principal);
    }

    /**
     * 效果同/user
     */
    @RequestMapping(value = "/user2")
    public AjaxResponse user2(Authentication authentication) {
        return AjaxResponse.success(authentication);
    }

    /**
     * 只打印用户属性信息，由于user-info-uri指定的接口中返回了自定义扩展属性，所以这里可以获取到扩展属性
     */
    @RequestMapping(value = "/user3")
    public AjaxResponse user3(Principal principal) {
        Map<String, Object> attributes = null;
        //返回扩展属性
        if (principal instanceof OAuth2AuthenticationToken) {
            attributes = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttributes();
            attributes.forEach((k, v) -> System.out.println(k + "=" + v));
        }
        return AjaxResponse.success(attributes);
    }

    /**
     * 只打印用户权限信息
     */
    @RequestMapping(value = "/user4")
    public AjaxResponse user4(Principal principal) {
        Collection<? extends GrantedAuthority> authorities = null;
        //返回权限信息
        if (principal instanceof OAuth2AuthenticationToken) {
            authorities = ((OAuth2AuthenticationToken) principal).getPrincipal().getAuthorities();
            authorities.forEach(s -> System.out.println(s.getAuthority()));
        }
        return AjaxResponse.success(authorities);
    }


    @GetMapping("/token")
    public AjaxResponse token(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        return AjaxResponse.success(authorizedClient.getAccessToken().getTokenValue());
    }
}
