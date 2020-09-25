package com.cas.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>认证用户工具类</p>
 * Created by hanqf on 2020/9/10 17:06.
 */


public class AuthenticationUtil {

    static {
        //SecurityContextHolder.MODE_THREADLOCAL 默认策略,这意味着security上下文对于相同的正在执行的线程的方法是可用的，即使security上下文没有显式地将参数传递给这些方法，如果希望在当前principal请求处理完毕后要清理这些线程，这样使用ThreadLocal局部变量会是非常安全的。
        //SecurityContextHolder.MODE_GLOBAL 表示SecurityContextHolder对象的全局的，应用中所有线程都可以访问；
        //SecurityContextHolder.MODE_INHERITABLETHREADLOCAL 用于线程有父子关系的情境中，线程希望自己的子线程和自己有相同的安全性。第一次请求会导致SecurityContextHolder.getContext()为null
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
    }

    /**
     * 取得当前用户名
     *
     * @return java.lang.String 当前用户名
     */
    public static String getUsername() {
        //SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            if (context instanceof SecurityContext) {
                SecurityContext sc = context;
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
                SecurityContext sc = context;
                Authentication auth = sc.getAuthentication();
                if (auth != null) {
                    return auth.isAuthenticated();
                }
            }
        }
        return false;
    }
}
