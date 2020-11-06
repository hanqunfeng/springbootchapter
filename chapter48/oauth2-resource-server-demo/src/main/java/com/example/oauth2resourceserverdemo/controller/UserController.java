package com.example.oauth2resourceserverdemo.controller;

import com.example.oauth2resourceserverdemo.exception.AjaxResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * <h1>res</h1>
 * Created by hanqf on 2020/11/6 17:22.
 */

@RestController
public class UserController {

    @RequestMapping(value = "/user")
    public AjaxResponse user(Principal principal) {
        //principal在经过security拦截后，是org.springframework.security.authentication.UsernamePasswordAuthenticationToken
        //在经OAuth2拦截后，是OAuth2Authentication
        return AjaxResponse.success(principal);
    }

}
