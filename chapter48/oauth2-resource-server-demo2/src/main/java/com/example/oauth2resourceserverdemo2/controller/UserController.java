package com.example.oauth2resourceserverdemo2.controller;

import com.example.oauth2resourceserverdemo2.exception.AjaxResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>res</h1>
 * Created by hanqf on 2020/11/6 17:22.
 */

@RestController
public class UserController {

    //注意权限区分大小写
    @PreAuthorize("hasRole('ROLE_admin') or hasRole('ROLE_user')")
    //@PreAuthorize("#oauth2.hasScope('any')") //不支持oauth2表达式
    @RequestMapping(value = "/user")
    public AjaxResponse user(Principal principal) {
        //principal在经过security拦截后，是org.springframework.security.authentication.UsernamePasswordAuthenticationToken
        //在经OAuth2拦截后，是OAuth2Authentication
        return AjaxResponse.success(principal);
    }

    @RequestMapping("/userInfo")
    public Map<String, String> userInfo(Principal principal){
        Map<String,String> map = new HashMap<>();
        map.put("username", principal.getName());
        return map;
    }

}
