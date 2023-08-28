package com.example.security;

/**
 * <h1>CustomServerAuthenticationEntryPoint</h1>
 * Created by hanqf on 2023/8/28 16:10.
 */


import com.example.exception.CustomException;
import com.example.exception.CustomExceptionType;
import com.example.response.AjaxResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * <h1>ServerAuthenticationEntryPoint</h1>
 * Created by hanqf on 2020/11/20 12:01.
 * <p>
 * token格式错误或过期时的处理方式
 */
public class CustomServerAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange serverWebExchange, AuthenticationException e) {
        try {
            return setErrorResponse(serverWebExchange.getResponse(), e.getMessage());
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected Mono<Void> setErrorResponse(ServerHttpResponse response, String message) throws JsonProcessingException {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        AjaxResponse ajaxResponse = AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR, message));
        return response.writeWith(Mono.just(response.bufferFactory().wrap(objectMapper.writeValueAsBytes(ajaxResponse))));

    }
}
