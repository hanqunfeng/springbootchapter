package com.example.jwtresourcewebfluxdemo.controller;


import com.example.jwtresourcewebfluxdemo.exception.AjaxResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * <h1>demo</h1>
 * Created by hanqf on 2020/11/3 16:42.
 */

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/hello")
    public Mono<AjaxResponse> hello(){
        return Mono.just(AjaxResponse.success("hello"));
    }

    @GetMapping("/user")
    public Mono<AjaxResponse> user(Principal principal){
        return Mono.just(AjaxResponse.success(principal));
    }
}
