package com.example.oauth2authserverdemo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>客户端认证获取用户信息</h1>
 * Created by hanqf on 2020/11/6 17:22.
 */

@RestController
public class UserController {

    @RequestMapping("/userInfo")
    public Map<String, String> userInfo(Authentication authentication){
        Map<String,String> map = new HashMap<>();
        Object principal = authentication.getPrincipal();
        if(principal instanceof Jwt){
            map.put("username", ((Jwt) principal).getClaim("user_name"));
        }

        return map;
    }

}
