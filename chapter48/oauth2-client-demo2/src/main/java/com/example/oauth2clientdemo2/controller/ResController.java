package com.example.oauth2clientdemo2.controller;

import com.example.oauth2clientdemo2.exception.AjaxResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * <h1>获取资源服务器数据</h1>
 * Created by hanqf on 2020/11/7 22:47.
 */

@RestController
@RequestMapping("/res")
public class ResController {

    private RestTemplate restTemplate;

    public ResController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * 获取资源服务器的数据
    */
    @RequestMapping("/res1")
    public AjaxResponse getRes(){
        return restTemplate.getForObject("http://localhost:8082/res/res1", AjaxResponse.class);
    }

    @RequestMapping("/user")
    public AjaxResponse getUser(){
        return restTemplate.postForObject("http://localhost:8082/user",null, AjaxResponse.class);
    }

    @RequestMapping("/rbac")
    public AjaxResponse getRbac(){
        return restTemplate.getForObject("http://localhost:8082/rbac", AjaxResponse.class);
    }

    @RequestMapping("/userInfo")
    public Map<String, Object> getuserInfo(){
        return restTemplate.getForObject("http://localhost:8082/userInfo", Map.class);
    }


}
