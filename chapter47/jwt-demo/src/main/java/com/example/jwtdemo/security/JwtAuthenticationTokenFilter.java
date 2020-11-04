package com.example.jwtdemo.security;

import com.example.jwtdemo.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt认证过滤器
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private CustomSecurityProperties customSecurityProperties;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException, CustomException {

        String jwtToken = request.getHeader(jwtTokenUtil.getHeader());
        if (!StringUtils.isEmpty(jwtToken)) {
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

            //如果可以正确的从JWT中提取用户信息，并且该用户未被授权
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtTokenUtil.validateToken(jwtToken, username)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    //给使用该JWT令牌的用户进行授权
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    //交给spring security管理,在之后的过滤器中不会再被拦截进行二次授权了
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
