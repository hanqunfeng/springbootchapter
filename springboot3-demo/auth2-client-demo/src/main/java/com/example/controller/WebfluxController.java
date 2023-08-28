package com.example.controller;

import com.example.exchange.WebfluxResourceServerClient;
import com.example.response.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 * Created by hanqf on 2023/8/28 16:51.
 */


@RestController
public class WebfluxController {

    @Autowired
    private WebfluxResourceServerClient webfluxResourceServerClient;

    /**
     * 请求资源服务器的内容
     */
    @RequestMapping("/resource-wf")
    public AjaxResponse resource() {
        return webfluxResourceServerClient.resource().block();
    }

    @RequestMapping("/jwt-wf")
    public AjaxResponse jwt() {
        System.out.println("jwt-wf");
        return webfluxResourceServerClient.jwt().block();
    }


    @RequestMapping("/user-wf")
    public AjaxResponse user() {
        return webfluxResourceServerClient.user().block();
    }

    @RequestMapping("/user2-wf")
    public AjaxResponse user2() {
        return webfluxResourceServerClient.user2().block();
    }

    @RequestMapping("/userInfo-wf")
    public AjaxResponse userInfo() {
        return webfluxResourceServerClient.userInfo().block();
    }

    @RequestMapping("/rbac-wf")
    public AjaxResponse rbac() {
        return webfluxResourceServerClient.rbac().block();
    }

}
