package com.example.service;

/**
 * <h1>JwtAuthService</h1>
 * Created by hanqf on 2023/8/21 12:41.
 */


import com.example.common.exception.CustomException;
import com.example.common.exception.CustomExceptionType;
import com.example.common.response.JwtResponse;
import com.example.security.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class JwtAuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtToken jwtToken;

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
        String token = jwtToken.generateToken(username);

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(token);
        jwtResponse.setExpiration(jwtToken.getExpirationDateFromToken(token));

        return jwtResponse;
    }


    /**
     * 刷新JWT令牌
     *
     * @return JwtResponse
     */
    public JwtResponse refreshToken(String oldToken) {
        //去掉前缀
        if (oldToken.startsWith("Bearer ")) {
            oldToken = oldToken.substring(7);
        }
        if (Boolean.FALSE.equals(jwtToken.isTokenExpired(oldToken))) {

            String token = jwtToken.refreshToken(oldToken);

            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setToken(token);
            jwtResponse.setExpiration(jwtToken.getExpirationDateFromToken(token));
            return jwtResponse;
        }
        return null;
    }


}
