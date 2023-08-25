package com.example.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * <h1>SecurityWebExceptionHandler</h1>
 * Created by hanqf on 2023/8/24 16:59.
 */

@Slf4j
@RestControllerAdvice
public class SecurityWebExceptionHandler {
    @ExceptionHandler(ClientAuthorizationRequiredException.class)
    public void clientAuthorizationRequiredException(ClientAuthorizationRequiredException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(),e);

        //跳转到登录页面
        response.sendRedirect("/login");
    }
}
