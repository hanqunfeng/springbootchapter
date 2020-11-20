package com.example.jwtresourcewebfluxdemo.security;

import com.example.jwtresourcewebfluxdemo.service.CustomReactiveUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * <h1></h1>
 * Created by hanqf on 2020/11/19 18:17.
 */


@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    @Autowired
    CustomReactiveUserDetailsService userDetailsService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String jwtToken = null;
        if (authentication.getCredentials() != null) {
            jwtToken = authentication.getCredentials().toString();
        }

        if (!StringUtils.isEmpty(jwtToken)) {
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

            if(!StringUtils.isEmpty(username)) {
                Mono<UserDetails> userDetailsMono = userDetailsService.findByUsername(username);
                //给使用该JWT令牌的用户进行授权
                Mono<Authentication> authenticationTokenMono = userDetailsMono
                        .map(userDetails ->
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                );

                return authenticationTokenMono;
            }
        }

        return Mono.empty();


    }
}

