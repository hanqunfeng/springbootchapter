package com.example.oauth2clientdemo.controller;

import com.example.oauth2clientdemo.exception.AjaxResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
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

    @GetMapping("/token")
    public AjaxResponse token(OAuth2Authentication authentication) {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        String tokenValue = details.getTokenValue();
        return AjaxResponse.success(tokenValue);
    }

}
