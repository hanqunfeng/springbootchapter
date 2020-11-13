package com.example.oauth2resourceserverdemo2.controller;

import com.example.oauth2resourceserverdemo2.exception.AjaxResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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
    @PreAuthorize("hasRole('admin') or hasRole('user')")
    //@PreAuthorize("#oauth2.hasScope('any')") //不支持oauth2表达式
    @RequestMapping(value = "/user")
    public AjaxResponse user(Principal principal) {
        //principal在经过security拦截后，是org.springframework.security.authentication.UsernamePasswordAuthenticationToken
        //在经OAuth2拦截后，是OAuth2Authentication
        return AjaxResponse.success(principal);
    }

    //注意权限区分大小写
    @PreAuthorize("hasAuthority('SCOPE_any')")
    @RequestMapping(value = "/user2")
    public AjaxResponse user2(Principal principal) {
        //principal在经过security拦截后，是org.springframework.security.authentication.UsernamePasswordAuthenticationToken
        //在经OAuth2拦截后，是OAuth2Authentication
        return AjaxResponse.success(principal);
    }

    /**
     * 获取用户的claim信息
    */
    @RequestMapping("/userInfo")
    public Map<String, Object> userInfo(Authentication authentication){
        Map<String,Object> map = new HashMap<>();
        Object principal = authentication.getPrincipal();
        if(principal instanceof Jwt){
            map.put("username", ((Jwt) principal).getClaim("user_name"));
            map.putAll(((Jwt) principal).getClaims());
        }

        return map;
    }

}
