package com.example.security.support;

import com.example.security.JwtProperties;
import com.example.security.JwtToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * jwt认证过滤器
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private UserDetailsService userDetailsService;
    private JwtToken jwtToken;
    private JwtProperties jwtProperties;

    public JwtAuthenticationTokenFilter(UserDetailsService userDetailsService, JwtToken jwtToken, JwtProperties jwtProperties) {
        this.userDetailsService = userDetailsService;
        this.jwtToken = jwtToken;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String jwtTokenStr = request.getHeader(jwtProperties.getHeader());
        if (StringUtils.hasText(jwtTokenStr)) {
            //去掉前缀
            if (jwtTokenStr.startsWith("Bearer ")) {
                jwtTokenStr = jwtTokenStr.substring(7);
            }

            String username = jwtToken.getUsernameFromToken(jwtTokenStr);

            //如果可以正确的从JWT中提取用户信息，并且该用户未被授权
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null &&
                    (Boolean.TRUE.equals(jwtToken.validateToken(jwtTokenStr, username)))) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                //给使用该JWT令牌的用户进行授权
                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                //此处不进行认证，直接交给spring security管理,在之后的过滤器中不会再被拦截进行二次授权了
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}
