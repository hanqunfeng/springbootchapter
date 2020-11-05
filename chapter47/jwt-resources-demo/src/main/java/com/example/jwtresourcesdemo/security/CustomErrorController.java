package com.example.jwtresourcesdemo.security;


import com.example.jwtresourcesdemo.exception.CustomException;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <h1>捕获/error中的异常</h1>
 * Created by hanqf on 2020/11/4 10:13.
 */

@RestController
public class CustomErrorController extends BasicErrorController {

    public CustomErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    /**
     * 请求头不含有jwt token数据，或者含有token但是token不合法，即无效或者过期就会执行如下方法
    */
    @Override
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL).including(ErrorAttributeOptions.Include.MESSAGE));
        HttpStatus status = getStatus(request);
        //return new ResponseEntity(body, status);
        throw new CustomException(status, body.getOrDefault("message","抱歉，您的token无效或过期").toString(), body);
    }
}
