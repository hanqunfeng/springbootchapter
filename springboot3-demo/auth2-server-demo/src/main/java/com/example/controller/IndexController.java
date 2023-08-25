package com.example.controller;

import com.example.response.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * <h1></h1>
 * Created by hanqf on 2023/8/22 17:20.
 */

@RestController
public class IndexController {

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @RequestMapping("/client")
    public AjaxResponse client() {
        final RegisteredClient client = registeredClientRepository.findByClientId("client");
        return AjaxResponse.success(client);
    }

    @RequestMapping("/")
    public String index() {
        return "hello world";
    }
    @RequestMapping("/demo")
    public String demo() {
        return "hello world demo";
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

}
