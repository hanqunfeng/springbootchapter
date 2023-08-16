package com.example.support.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * <h1>RbasService</h1>
 * Created by hanqf on 2020/11/7 01:22.
 * <p>
 * 基于权限管理模型的认证
 */

@Service("rbacService")
public class CP_RbacService {

    /**
     * <h2>权限验证规则</h2>
     *
     * @param request
     * @param authentication
     * @return java.lang.Boolean
     */
    public Boolean hasPerssion(HttpServletRequest request, Authentication authentication) {

        boolean hasPerssion = false;
        String userId = authentication.getName();
        //获得当前用户的可访问资源，自定义的查询方法，之后和当前请求资源进行匹配，成功则放行，否则拦截
        if (userId != null && !"anonymousUser".equals(userId)) {
            hasPerssion = true;
        }

        return hasPerssion;
    }

}
