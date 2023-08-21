package com.example.security.support;

import com.example.common.exception.CustomException;
import com.example.common.response.AjaxResponse;
import com.example.security.JwtProperties;
import com.example.security.JwtToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt认证过滤器
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    private UserDetailsService userDetailsService;
    private JwtToken jwtToken;
    private JwtProperties jwtProperties;
    private ObjectMapper objectMapper;
    private CustomSecurityProperties securityProperties;

    public JwtAuthenticationTokenFilter(UserDetailsService userDetailsService, JwtToken jwtToken, JwtProperties jwtProperties, ObjectMapper objectMapper, CustomSecurityProperties securityProperties) {
        this.userDetailsService = userDetailsService;
        this.jwtToken = jwtToken;
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
        this.securityProperties = securityProperties;
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
            } else {
                checkError(request, response);
            }
        } else {
            checkError(request, response);
        }
        filterChain.doFilter(request, response);
    }

    private void checkError(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        boolean check = false;
        String url = request.getServletPath();//当前请求的URL
        for (String key : securityProperties.getPermitAll()) {
            boolean matched = antPathMatcher.match(key, url);
            if (matched) {
                check = true;
                break;
            }
        }

        if (!check) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", new Date());
            body.put("status", 403);
            body.put("error", "Forbidden");
            body.put("message", "");
            body.put("path", request.getRequestURI());

            AjaxResponse ajaxResponse = AjaxResponse.error(new CustomException(HttpStatus.FORBIDDEN, "抱歉，您的token无效或过期", body));
            response.setStatus(403);
            response.setContentType("application/json;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(objectMapper.writeValueAsString(ajaxResponse));
            }
        }
    }

}
