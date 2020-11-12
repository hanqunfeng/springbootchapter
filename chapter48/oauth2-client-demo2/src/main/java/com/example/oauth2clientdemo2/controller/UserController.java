package com.example.oauth2clientdemo2.controller;


import com.example.oauth2clientdemo2.exception.AjaxResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * <h1>res</h1>
 * Created by hanqf on 2020/11/6 17:22.
 */

@RestController
public class UserController {

    @PreAuthorize("#oauth2.hasScope('any')")
    @RequestMapping(value = "/user")
    public AjaxResponse user(Principal principal) {
        //principal在经过security拦截后，是org.springframework.security.authentication.UsernamePasswordAuthenticationToken
        //在经OAuth2拦截后，是OAuth2Authentication
        return AjaxResponse.success(principal);
    }

    @RequestMapping(value = "/user2")
    public AjaxResponse user2(Authentication authentication) {
        return AjaxResponse.success(authentication);
    }


    @GetMapping("/token")
    public AjaxResponse token(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        return AjaxResponse.success(authorizedClient.getAccessToken().getTokenValue());
    }
}
