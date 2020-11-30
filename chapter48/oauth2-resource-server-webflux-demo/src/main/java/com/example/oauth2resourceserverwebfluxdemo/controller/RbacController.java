package com.example.oauth2resourceserverwebfluxdemo.controller;


import com.example.oauth2resourceserverwebfluxdemo.exception.AjaxResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * <h1></h1>
 * Created by hanqf on 2020/11/7 01:24.
 */

@RestController
public class RbacController {

    @GetMapping("/rbac")
    public Mono<AjaxResponse> demo(){
        return Mono.just(AjaxResponse.success("rbac"));
    }
}
