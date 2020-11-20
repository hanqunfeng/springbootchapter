package com.example.jwtresourcewebfluxdemo.security;

import com.example.jwtresourcewebfluxdemo.exception.AjaxResponse;
import com.example.jwtresourcewebfluxdemo.exception.CustomException;
import com.example.jwtresourcewebfluxdemo.exception.CustomExceptionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * <h1>没有权限时的处理方式</h1>
 * Created by hanqf on 2020/11/20 11:56.
 */

@Component
public class CustomServerAccessDeniedHandler implements ServerAccessDeniedHandler {
    @SneakyThrows
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, AccessDeniedException e) {
        return setErrorResponse(serverWebExchange.getResponse(),e.getMessage());
    }

    protected Mono<Void> setErrorResponse(ServerHttpResponse response, String message) throws JsonProcessingException {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        AjaxResponse ajaxResponse = AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR, message));
        return response.writeWith(Mono.just(response.bufferFactory().wrap(objectMapper.writeValueAsBytes(ajaxResponse))));

    }
}
