package com.example.oauth2clientwebfluxdemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * <h1>demo</h1>
 * Created by hanqf on 2020/11/7 13:35.
 */

@RestController
public class DemoController {

    @RequestMapping("/")
    public Mono<String> index(){
        return Mono.just("index");
    }


}
