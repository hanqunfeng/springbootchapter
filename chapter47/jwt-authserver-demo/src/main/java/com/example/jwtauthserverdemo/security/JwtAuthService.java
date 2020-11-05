package com.example.jwtauthserverdemo.security;


import com.example.jwtauthserverdemo.exception.CustomException;
import com.example.jwtauthserverdemo.exception.CustomExceptionType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class JwtAuthService {

    @Resource
    JwtTokenUtil jwtTokenUtil;

    /**
     * 登录认证换取JWT令牌
     *
     * @return JwtResponse
     */
    public JwtResponse login(String username, String password) throws CustomException {

        //验证用户名密码是否有效
        if (!((username.equals("admin") && password.equals("123456"))
                || (username.equals("guest") && password.equals("123456")))) {
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
