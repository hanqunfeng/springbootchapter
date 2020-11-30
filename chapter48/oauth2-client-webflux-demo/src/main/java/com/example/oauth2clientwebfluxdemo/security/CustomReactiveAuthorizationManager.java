package com.example.oauth2clientwebfluxdemo.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

/**
 * <h1>ReactiveAuthorizationManager</h1>
 * Created by hanqf on 2020/11/30 12:05.
 */

@Component
public class CustomReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        return authentication.map(auth -> {
            ServerHttpRequest request = object.getExchange().getRequest();
            Object principal = auth.getPrincipal();
            String username;
            if (principal instanceof Jwt) {
                username = ((Jwt) principal).getClaimAsString("user_name");
            } else {
                username = principal.toString();
            }
            boolean hasPerssion = false;
            if (StringUtils.hasText(username) && !"anonymousUser".equals(username)) {
                //根据用户名查询用户资源权限，这里应该访问数据库查询
                Set<String> uris = new HashSet<>();
                for (String uri : uris) {
                    //验证用户拥有的资源权限是否与请求的资源相匹配
                    if (new AntPathMatcher().match(uri, request.getURI().toString())) {
                        hasPerssion = true;
                        break;
                    }
                }
            }

            //暂时全部返回true
            hasPerssion = true;
            return new AuthorizationDecision(hasPerssion);
        });
    }
}
