package com.example.support;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Map;

/**
 * <p>参数拦截器</p>
 * Created by hanqf on 2020/6/22 16:57.
 */

@Slf4j
public class CP_InitializingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        // TODO Auto-generated method stub

        // log追加：用户名 - sessionID - IP - URL - 请求参数
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(request.getSession().getId());
        stringBuilder.append(" - ").append(request.getRemoteAddr());
        stringBuilder.append(" - ").append(request.getServletPath());
        if (request.getQueryString() != null) {
            stringBuilder.append(" - ").append(request.getQueryString()).append(" - ");
        } else {
            Map<String, String[]> parameters = request.getParameterMap();
            if (!parameters.isEmpty()) {
                stringBuilder.append(" - [");
            }
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String message = "";
                if (value.getClass().isArray()) {
                    Object[] args = (Object[]) value;
                    message = " " + key + "=" + Arrays.toString(args) + " ";
                } else {
                    message = key + "=" + value + " ";
                }
                stringBuilder.append(message);
            }
            if (!parameters.isEmpty()) {
                stringBuilder.append("]");
            }
        }
        //将localeContextHolder设置为线程继承状态
        LocaleContextHolder.setLocale(LocaleContextHolder.getLocale(), true);
        log.info(stringBuilder.toString());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub
        request.setAttribute("_contextPath", request.getContextPath());
        String serverName = "http://" + request.getServerName();
        String serverPort = ":" + request.getServerPort();
        String httpPath = serverName + serverPort ;
        request.setAttribute("_serverPath", httpPath);
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub

    }

}
