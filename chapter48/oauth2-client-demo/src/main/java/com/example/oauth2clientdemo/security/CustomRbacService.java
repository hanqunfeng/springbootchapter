package com.example.oauth2clientdemo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * <h1>RbasService</h1>
 * Created by hanqf on 2020/11/7 01:22.
 * <p>
 * 基于权限管理模型的认证
 */

@Service("rbacService")
public class CustomRbacService {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public Boolean hasPerssion(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        boolean hasPerssion = false;

        if (StringUtils.hasText(username) && !"anonymousUser".equals(username)) {
            //根据用户名查询用户资源权限，这里应该访问数据库查询
            Set<String> uris = new HashSet<>();
            for (String uri : uris) {
                //验证用户拥有的资源权限是否与请求的资源相匹配
                if (antPathMatcher.match(uri, request.getRequestURI())) {
                    hasPerssion = true;
                    break;
                }
            }

            //暂时全部返回true
            return true;
        }

        return hasPerssion;
    }
}
