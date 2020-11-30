package com.example.oauth2resourceserverwebfluxdemo.controller;


import com.example.oauth2resourceserverwebfluxdemo.exception.AjaxResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * <h1>res</h1>
 * Created by hanqf on 2020/11/6 17:22.
 */

@RestController
@RequestMapping(value = "/res")
public class ResController {

    @GetMapping("/res1")
    public Mono<AjaxResponse> res1(){
        return Mono.just(AjaxResponse.success("res1"));
    }

    @PostMapping("/res2")
    public Mono<AjaxResponse> res2(){
        return Mono.just(AjaxResponse.success("res2"));
    }

}
