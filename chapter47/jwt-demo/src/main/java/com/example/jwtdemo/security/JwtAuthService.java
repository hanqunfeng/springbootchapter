package com.example.jwtdemo.security;


import com.example.jwtdemo.exception.CustomException;
import com.example.jwtdemo.exception.CustomExceptionType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class JwtAuthService {

    @Resource
    AuthenticationManager authenticationManager;

    @Resource
    JwtTokenUtil jwtTokenUtil;

    /**
     * 登录认证换取JWT令牌
     *
     * @return JwtResponse
     */
    public JwtResponse login(String username, String password) throws CustomException {
        try {
            //使用用户名密码进行登录验证
            UsernamePasswordAuthenticationToken upToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            //验证用户名和密码是否正确，正确返回认证主体，错误抛出AuthenticationException
            Authentication authentication = authenticationManager.authenticate(upToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR
                    , "用户名或者密码不正确");
        }

        //生成jwt返回
        String token = jwtTokenUtil.generateToken(username);

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(token);
        jwtResponse.setExpiration(jwtTokenUtil.getExpirationDateFromToken(token));

        return jwtResponse;
    }


    /**
     * 刷新JWT令牌
     *
     * @return JwtResponse
     */
    public JwtResponse refreshToken(String oldToken) {
        if (!jwtTokenUtil.isTokenExpired(oldToken)) {

            String token = jwtTokenUtil.refreshToken(oldToken);

            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setToken(token);
            jwtResponse.setExpiration(jwtTokenUtil.getExpirationDateFromToken(token));
            return jwtResponse;
        }
        return null;
    }


}
