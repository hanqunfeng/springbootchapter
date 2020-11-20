package com.example.jwtresourcewebfluxdemo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * <h1>JwtSecurityContextRepository</h1>
 * Created by hanqf on 2020/11/20 10:00.
 */


@Component
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {
    @Autowired
    private JwtAuthenticationManager jwtAuthenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        //获取上下文中的token
        String token = serverWebExchange.getAttribute("token");

        if (token != null) {
            //将token传入AuthenticationManager的authenticate方法
            return this.jwtAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(token, token)).map(SecurityContextImpl::new);
        } else {
            return Mono.empty();
        }
    }
}

