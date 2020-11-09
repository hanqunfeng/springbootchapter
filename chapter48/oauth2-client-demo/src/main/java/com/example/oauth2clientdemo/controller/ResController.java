package com.example.oauth2clientdemo.controller;

import com.example.oauth2clientdemo.exception.AjaxResponse;
import com.example.oauth2clientdemo.security.OAuth2ResourceRestTemplateUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>获取资源服务器数据</h1>
 * Created by hanqf on 2020/11/7 22:47.
 */

@RestController
@RequestMapping("/res")
public class ResController {


    /**
     * 获取资源服务器的数据
    */
    @RequestMapping("/res1")
    public AjaxResponse getRes(){
        return OAuth2ResourceRestTemplateUtil.getRestTemplate().getForObject("http://localhost:8081/res/res1", AjaxResponse.class);
    }

    @RequestMapping("/user")
    public AjaxResponse getUser(){
        return OAuth2ResourceRestTemplateUtil.getRestTemplate().postForObject("http://localhost:8081/user",null, AjaxResponse.class);
    }

    @RequestMapping("/rbac")
    public AjaxResponse getRbac(){
        return OAuth2ResourceRestTemplateUtil.getRestTemplate().getForObject("http://localhost:8081/rbac", AjaxResponse.class);
    }


}
