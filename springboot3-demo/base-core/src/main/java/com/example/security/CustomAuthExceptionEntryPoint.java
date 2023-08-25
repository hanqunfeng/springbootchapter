package com.example.security;

/**
 * <h1>CustomAuthExceptionEntryPoint</h1>
 * Created by hanqf on 2023/8/24 16:04.
 */


import com.example.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.response.AjaxResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>AuthExceptionEntryPoint</h1>
 * Created by hanqf on 2020/11/6 23:27.
 *
 * token无效或过期
 */

public class CustomAuthExceptionEntryPoint implements AuthenticationEntryPoint
{

    private ObjectMapper objectMapper;

    public CustomAuthExceptionEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", 401);

        body.put("message", authException.getMessage());
        body.put("path", request.getRequestURI());

        //Throwable cause = authException.getCause();
        body.put("error", "Forbidden");

        AjaxResponse ajaxResponse = AjaxResponse.error(new CustomException(HttpStatus.UNAUTHORIZED, "抱歉，您没有访问该接口的权限", body));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(ajaxResponse));
        }
    }
}
