package com.example.controller;

import com.example.response.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * <h1></h1>
 * Created by hanqf on 2023/8/23 16:19.
 */

@RestController
public class ResourceController {

    @Autowired
    private ReactiveJwtDecoder jwtDecoder;

    @GetMapping("/")
    public Mono<AjaxResponse> home() {
        LocalDateTime time = LocalDateTime.now();
        return Mono.just(AjaxResponse.success("Welcome Resource Server! - " + time));
    }

    @GetMapping("/resource")
    public Mono<AjaxResponse> resource(@RequestHeader("Authorization") String token) {
        LocalDateTime time = LocalDateTime.now();
        return Mono.just(AjaxResponse.success("Welcome Resource Server! - " + time + " <br> token - " + token));
    }

    @GetMapping("/jwt")
    public Mono<AjaxResponse> jwt(@RequestHeader("Authorization") String token) {
        //去掉前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Jwt jwt = null;
        try {
            // block() 方法会报错
            jwt = jwtDecoder.decode(token).toFuture().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("tokenValue", jwt.getTokenValue());
        map.put("claims", jwt.getClaims());
        map.put("headers", jwt.getHeaders());
        map.put("exp", jwt.getExpiresAt());
        map.put("aud", jwt.getAudience());
        map.put("iat", jwt.getIssuedAt());
        map.put("iss", jwt.getIssuer().toString());
        map.put("nbf", jwt.getNotBefore());
        map.put("subject", jwt.getSubject());
        map.put("id", jwt.getId());
        return Mono.just(AjaxResponse.success(map));
    }

    @GetMapping("/demo")
    public Mono<AjaxResponse> demo() {
        LocalDateTime time = LocalDateTime.now();
        return Mono.just(AjaxResponse.success("Demo Welcome Resource Server! - " + time));
    }

}
