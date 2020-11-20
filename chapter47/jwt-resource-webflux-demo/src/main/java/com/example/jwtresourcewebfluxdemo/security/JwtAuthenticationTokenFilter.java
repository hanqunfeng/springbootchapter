package com.example.jwtresourcewebfluxdemo.security;


import com.example.jwtresourcewebfluxdemo.exception.AjaxResponse;
import com.example.jwtresourcewebfluxdemo.exception.CustomException;
import com.example.jwtresourcewebfluxdemo.exception.CustomExceptionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * jwt认证过滤器
 */
@Component
public class JwtAuthenticationTokenFilter implements WebFilter {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtProperties jwtProperties;


    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        if (request.getPath().value().contains("login")) {
            return webFilterChain.filter(serverWebExchange);
        }
        ServerHttpResponse response = serverWebExchange.getResponse();
        String jwtToken = request.getHeaders().getFirst(jwtProperties.getHeader());

        if (StringUtils.isEmpty(jwtToken)) {
            return this.setErrorResponse(response, "未携带token");
        }

        //去掉前缀
        if (jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

        if (!StringUtils.isEmpty(username) && jwtTokenUtil.validateToken(jwtToken, username)) {
            //获取token后将其保存到上下文中，后面会由JwtSecurityContextRepository取出token并调用JwtAuthenticationManager的authenticate方法进行用户认证
            serverWebExchange.getAttributes().put("token", jwtToken);
            return webFilterChain.filter(serverWebExchange);
        }else {
            return this.setErrorResponse(response, "token格式错误或过期");
        }


    }

    protected Mono<Void> setErrorResponse(ServerHttpResponse response, String message) throws JsonProcessingException {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        AjaxResponse ajaxResponse = AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR, message));
        return response.writeWith(Mono.just(response.bufferFactory().wrap(objectMapper.writeValueAsBytes(ajaxResponse))));

    }


}
