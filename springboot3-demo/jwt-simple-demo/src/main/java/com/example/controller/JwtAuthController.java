package com.example.controller;

import com.example.common.exception.CustomException;
import com.example.common.exception.CustomExceptionType;
import com.example.common.response.AjaxResponse;
import com.example.model.User;
import com.example.service.JwtAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>JwtAuthController</h1>
 * Created by hanqf on 2023/8/21 12:40.
 */


@RestController
public class JwtAuthController {

    @Autowired
    private JwtAuthService jwtAuthService;


    /**
     * json请求：
     * {
     * "username":"admin",
     * "password":"123456"
     * }
     */
    @RequestMapping(value = "/authentication")
    public AjaxResponse login(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        if (!StringUtils.hasText(username)
                || !StringUtils.hasText(password)) {
            return AjaxResponse.error(
                    new CustomException(CustomExceptionType.USER_INPUT_ERROR,
                            "用户名或者密码不能为空"));
        }
        try {
            return AjaxResponse.success(jwtAuthService.login(username, password));
        } catch (CustomException e) {
            return AjaxResponse.error(e);
        }
    }

    @RequestMapping(value = "/refresh_token")
    public AjaxResponse refresh(@RequestHeader("${jwt.header}") String token) {
        return AjaxResponse.success(jwtAuthService.refreshToken(token));
    }

}
