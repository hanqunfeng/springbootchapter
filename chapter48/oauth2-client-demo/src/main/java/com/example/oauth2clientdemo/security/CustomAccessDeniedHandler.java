package com.example.oauth2clientdemo.security;


import com.example.oauth2clientdemo.exception.AjaxResponse;
import com.example.oauth2clientdemo.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>重写访问拒绝拦截器</h1>
 * Created by hanqf on 2020/11/3 17:07.
 * <p>
 * 登录验证成功，即token合法，但是没有权限访问资源时执行
 */

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", 403);
        body.put("error", "Forbidden");
        body.put("message", e.getMessage());
        body.put("path", request.getRequestURI());

        AjaxResponse ajaxResponse = AjaxResponse.error(new CustomException(HttpStatus.FORBIDDEN, "抱歉，您没有访问该接口的权限", body));
        response.setStatus(403);
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(ajaxResponse));
        }
    }
}