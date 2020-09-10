package com.example.springsecuritydemo.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>认证用户工具类</p>
 * Created by hanqf on 2020/9/10 17:06.
 */


public class AuthenticationUtil {
    /**
     * 取得当前用户名
     *
     * @return java.lang.String 当前用户名
     */
    public static String getUsername() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            if (context instanceof SecurityContext) {
                SecurityContext sc = (SecurityContext) context;
                Authentication auth = sc.getAuthentication();
                if (auth != null) {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof UserDetails) {
                        return ((UserDetails) principal).getUsername();
                    } else {
                        return principal.toString();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断用户是否已经通过认证。
     *
     * @return boolean 是否通过认证标记
     */
    public static boolean isAuthenticated() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            if (context instanceof SecurityContext) {
                SecurityContext sc = (SecurityContext) context;
                Authentication auth = sc.getAuthentication();
                if (auth != null) {
                    return auth.isAuthenticated();
                }
            }
        }
        return false;
    }
}
