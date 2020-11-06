package com.example.oauth2authserverdemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <h1>redirect</h1>
 * Created by hanqf on 2020/11/5 17:12.
 */

@RestController
public class OAuth2RedirectController {

    /**
     * <h2>回调路径</h2>
     * Created by hanqf on 2020/11/6 09:52. <br>
     *
     * @param data 接收数据
     * @param response 响应状态
     * @return org.springframework.http.ResponseEntity&lt;java.util.Map&lt;java.lang.String,java.lang.Object&gt;&gt;
     * @author hanqf
     */
    @RequestMapping("/redirect")
    public ResponseEntity<Map<String,Object>> redirect(@RequestParam Map<String, Object> data, HttpServletResponse response){
        return new ResponseEntity(data, HttpStatus.valueOf(response.getStatus()));
    }
}
