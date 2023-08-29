package com.example.controller;

import com.example.exchange.WebfluxResourceServerClient;
import com.example.response.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
    public Mono<AjaxResponse> resource() {
        return webfluxResourceServerClient.resource();
    }

    @RequestMapping("/jwt-wf")
    public Mono<AjaxResponse> jwt() {
        System.out.println("jwt-wf");
        return webfluxResourceServerClient.jwt();
    }


    @RequestMapping("/user-wf")
    public Mono<AjaxResponse> user() {
        return webfluxResourceServerClient.user();
    }

    @RequestMapping("/user2-wf")
    public Mono<AjaxResponse> user2() {
        return webfluxResourceServerClient.user2();
    }

    @RequestMapping("/userInfo-wf")
    public Mono<AjaxResponse> userInfo() {
        return webfluxResourceServerClient.userInfo();
    }

    @RequestMapping("/rbac-wf")
    public Mono<AjaxResponse> rbac()  {
        return webfluxResourceServerClient.rbac();
    }

}
